package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.itf.IPaCoExtOFProgram;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.utils.debug.RenderDebugger;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.EXTDebugLabel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader.ABSTRACT_INST;

public class OFTemplatedProgram extends BaseProgram {
	private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Templated Vanilla/Iris");
	public static OFTemplatedProgram useProgram;
	public Map<String, Integer> attributeLocations = new HashMap<>();
	
	Runnable firstBind;
	Runnable preBind;
	Runnable postBind;
	Runnable postClear;
	
	public OFTemplatedProgram setFirstBind(Runnable firstBind) {
		this.firstBind = firstBind;
		return this;
	}
	
	public OFTemplatedProgram setPreBind(Runnable preBind) {
		this.preBind = preBind;
		return this;
	}
	
	public OFTemplatedProgram setPostBind(Runnable postBind) {
		this.postBind = postBind;
		return this;
	}
	
	public OFTemplatedProgram setPostClear(Runnable postClear) {
		this.postClear = postClear;
		return this;
	}
	
	final Program from;
	final int id;
	Map<Uniform, Integer> uniformModCounts = new HashMap<>();
	
	public OFTemplatedProgram(Program from, List<ShaderAttachment> attachments) {
		this.from = from;
		
		id = GL20.glCreateProgram();
//		EXTDebugLabel.glLabelObjectEXT(EXTDebugLabel.GL_PROGRAM_OBJECT_EXT, id, "PACO_SHADER");
		
		for (ShaderAttachment attachment : attachments) {
			GL20.glAttachShader(id, attachment.getId());
		}
	}
	
	public void validate(String mode) {
		int i = GlStateManager.glGetProgrami(id, 35714);
		if (i == 0) {
			LOGGER.warn("({}) Error encountered when linking program containing VS {} and FS {}. Log output:", mode, "TODO", "TODO");
			LOGGER.warn(GlStateManager.glGetProgramInfoLog(id, 32768));
		}
	}
	
	public void link(Program vanilla, VariableMapper mapper, TemplateShaderResourceLoader.TemplateStruct transformation) {
//		int index = 0;
//
//		VertexFormat vertexFormat = OptifineAccessor.getEntityShader();
//		VertexFormat vertexFormat = DefaultVertexFormat.NEW_ENTITY;
//		List<String> attributeNames = vertexFormat.getElementAttributeNames();
//
//		for (int i = 0; i < attributeNames.size(); ++i) {
//			String name = attributeNames.get(i);
//			VertexFormatElement element = ((IVertexFormatAccess) vertexFormat).pandoraCore$getMappings().get(name);
//			int attributeIndex = ((IVertexFormatElementAccess) element).pandoraCore$getIndex();
//			if (attributeIndex >= 0) {
//				String nameOf = "va" + name;
//				Uniform.glBindAttribLocation(id, attributeIndex, nameOf);
//			}
//
//			index = Math.max(index, attributeIndex + 1);
//		}

		int index = 0;
		for (String elementAttributeName : DefaultVertexFormat.NEW_ENTITY.getElementAttributeNames()) {
			Uniform.glBindAttribLocation(id, index++, mapper.mapTo(null, elementAttributeName));
		}
		IPaCoExtOFProgram extProg = (IPaCoExtOFProgram)vanilla;
		for (OFVtxAttribute value : OFVtxAttribute.values()) {
			int idx = value.id.get();
			if (extProg.pandoraCore$usesAttrib(value)) {
				Uniform.glBindAttribLocation(id, idx, value.ofName);
				RenderDebugger.checkError(value.name());
			}
			if (index <= idx) {
				index = idx + 1;
			}
		}
		TemplatedShader.linkAttributes(id, index, transformation);
		
		GL20.glLinkProgram(id);
		TemplatedShader.bindAttributes(attributeLocations, id, transformation);
	}
	
	Program prevProg;
	
	public void bind() {
		RenderDebugger.checkError("pre-bind");
		if (firstBind != null) {
			firstBind.run();
			firstBind = null;
		}

		useProgram = this;
		prevProg = Shaders.activeProgram;

		GL20.glUseProgram(id);

		OptifineAccessor.falseBind(from);

		if (preBind != null)
			preBind.run();
		OptifineAccessor.falseBind(Shaders.ProgramNone);

		// TODO: I believe I do need to bind a proper vanilla shader
		//       for now, I'll just go entity shader
		OptifineAccessor.falseUnbind();
		RenderSystem.setShader(GameRenderer::getRendertypeEntitySolidShader);
		Shaders.useProgram(from);

		OptifineAccessor.bindGbuffersTextures();

		if (postBind != null)
			postBind.run();
	}
	
	public void close() {
		for (Uniform pacoUform : pacoUforms) {
			pacoUform.close();
		}
		GL20.glDeleteProgram(id);
	}
	
	public void clear() {
		RenderDebugger.checkError("pre clear");
		if (postClear != null)
			postClear.run();
		useProgram = null;
		Shaders.useProgram(prevProg);
		RenderSystem.setShader(() -> null);
	}
	
	Map<String, AbstractUniform> uniforms = new Object2ObjectRBTreeMap<>();
	ArrayList<Uniform> pacoUforms = new ArrayList<>();
	
	public AbstractUniform getUniform(String name, int type, int count) {
		AbstractUniform paco = uniforms.get(name);
		if (paco != null) {
			return paco;
		}

//		Uniform uForm = from.getUniform(name);
		Uniform uForm = null;
		if (uForm != null) uniforms.put(name, uForm);
		else {
			int loc = GL20.glGetUniformLocation(id, name);
			if (loc != -1) {
				uForm = new Uniform(name, type, count, null);
				uForm.setLocation(loc);
				pacoUforms.add(uForm);
				uniforms.put(name, uForm);
			} else {
				uniforms.put(name, ABSTRACT_INST);
				return ABSTRACT_INST;
			}
		}
		
		return uForm;
	}
	
	public void upload() {
		try {
			for (Uniform pacoUform : pacoUforms) {
				pacoUform.upload();
			}
		} catch (Throwable err) {
		}
	}
	
	public int getAttributeLocation(String name) {
		return attributeLocations.getOrDefault(name, -1);
	}
	
	public int getId() {
		return id;
	}
}

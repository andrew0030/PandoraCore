package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoUniformListable;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader.ABSTRACT_INST;

public class TemplatedProgram extends BaseProgram {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Templated Vanilla/Iris");
    public Map<String, Integer> attributeLocations = new HashMap<>();

    Runnable firstBind;
    Runnable preBind;
    Runnable postBind;
    Runnable postClear;

    public TemplatedProgram setFirstBind(Runnable firstBind) {
        this.firstBind = firstBind;
        return this;
    }
	
	public TemplatedProgram setPreBind(Runnable preBind) {
		this.preBind = preBind;
		return this;
	}
	
	public TemplatedProgram setPostBind(Runnable postBind) {
		this.postBind = postBind;
		return this;
	}
	
	public TemplatedProgram setPostClear(Runnable postClear) {
		this.postClear = postClear;
		return this;
	}
	
	ShaderInstance from;
    int id;
	Map<Uniform, Integer> uniformModCounts = new HashMap<>();

    public TemplatedProgram(ShaderInstance from, List<ShaderAttachment> attachments) {
        this.from = from;

        id = GL20.glCreateProgram();

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

    public void link(ShaderInstance vanilla, VariableMapper mapper, TemplateShaderResourceLoader.TemplateStruct transformation) {
        int index = 0;
        for (String elementAttributeName : vanilla.getVertexFormat().getElementAttributeNames()) {
            Uniform.glBindAttribLocation(id, index++, mapper.mapTo(null, elementAttributeName));
        }
        GL20.glLinkProgram(id);
        TemplatedShader.bindAttributes(this, id, index, transformation);
    }

    public void bind() {
	    if (firstBind != null) {
		    firstBind.run();
		    firstBind = null;
	    }
	    
	    for (Uniform uniform : ((IPaCoUniformListable) from).pandoraCore$listUniforms()) {
		    IPaCoModTracker mt = (IPaCoModTracker) uniform;
		    Integer i = uniformModCounts.getOrDefault(uniform, null);
			
			mt.pandoraCore$isolate();
			
		    if (i != null) {
			    if (mt.pandoraCore$dirtyIfNotMod(i)) {
				    // update mod count
				    uniformModCounts.put(uniform, mt.pandoraCore$mod());
				    ((IPaCoModTracker) uniform).pandoraCore$enableModTracking();
			    }
		    } else {
			    // cache mod and assume it needs to be uploaded
			    uniformModCounts.put(uniform, mt.pandoraCore$mod());
			    ((IPacoDirtyable) uniform).pandoraCore$markDirty();
			    ((IPaCoModTracker) uniform).pandoraCore$enableModTracking();
		    }
	    }
		from.markDirty();
		
        ((IPaCoConditionallyBindable) from).pandoraCore$disableBind();
		
//	    IrisRenderSystem.unbindAllSamplers();
//	    IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, 0);
//	    IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 1, 0);
//	    IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, 0);
		
	    if (preBind != null)
			preBind.run();
		
	    GL20.glUseProgram(id);
//		if (false) {
			RenderSystem.setShader(() -> from);
			from.apply();
//		}
	    
	    if (postBind != null)
		    postBind.run();
	    
//	    PBRTextureManager.notifyPBRTexturesChanged();
    }

    public void close() {
        for (Uniform pacoUform : pacoUforms) {
            pacoUform.close();
        }
        GL20.glDeleteProgram(id);
    }

    public void clear() {
        ((IPaCoConditionallyBindable) from).pandoraCore$enableBind();
        from.clear();
		if (postClear != null)
		    postClear.run();
	    for (Uniform uniform : ((IPaCoUniformListable) from).pandoraCore$listUniforms()) {
		    IPaCoModTracker mt = (IPaCoModTracker) uniform;
		    mt.pandoraCore$release();
	    }
	    RenderSystem.setShader(() -> null);
    }


    Map<String, AbstractUniform> uniforms = new Object2ObjectRBTreeMap<>();
    ArrayList<Uniform> pacoUforms = new ArrayList<>();

    public AbstractUniform getUniform(String name, int type, int count) {
        AbstractUniform paco = uniforms.get(name);
        if (paco != null) {
            return paco;
        }

        Uniform uForm = from.getUniform(name);
        if (uForm != null) uniforms.put(name, uForm);
        else {
            int loc = GL20.glGetUniformLocation(id, name);
            if (loc != -1) {
                uForm = new Uniform(name, type, count, from);
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
}

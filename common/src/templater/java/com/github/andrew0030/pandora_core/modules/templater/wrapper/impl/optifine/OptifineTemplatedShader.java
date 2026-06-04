package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.*;
import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.itf.ILocationedObject;
import com.github.andrew0030.pandora_core.modules.templater.itf.INamedShader;
import com.github.andrew0030.pandora_core.modules.templater.itf.PaCoOFUniformListable;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.blackhole.VoidShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import net.optifine.shaders.Program;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniformBase;
import net.optifine.shaders.uniform.ShaderUniforms;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OptifineTemplatedShader extends TemplatedShader {
	protected static boolean FIRST_BIND = false;
	
	OFTemplatedProgram program;
	BaseProgram programShadow;
	List<String> sourceNames = new ArrayList<>();
	
	public OptifineTemplatedShader(
			VariableMapper mapper,
			TemplateLoader loader,
			Map<String, String> transformers,
			Function<String, TemplateTransformation> transformations,
			TemplateShaderResourceLoader.TemplateStruct struct,
			TransformationProcessor processor,
			String template, Program vanilla, AttachmentSpecifier[] specifiers,
			String templateShadow, Program vanillaShadow, AttachmentSpecifier[] specifiersShadow
	) {
		super(loader, struct, template);
		
		// load base shader
		{
			List<ShaderAttachment> attachments = new ArrayList<>();
			for (AttachmentSpecifier specifier : specifiers) {
				if (specifier == null) continue;
				
				TemplateTransformation apply = struct.getTransformation(
						specifier.type.strName(), transformers, transformations
				);
				ShaderAttachment attachment = new ShaderAttachment(
						specifier.source, specifier.type,
						apply, (INamedShader) vanilla,
						specifier.preprocess, mapper,
						processor, struct.location,
						"optifine/main/"
				);
				attachments.add(attachment);
				
				String srcFl = specifier.fileName + "." + specifier.type.strName();
				sourceNames.add(srcFl);
			}
			
			// make program
			program = new OFTemplatedProgram(
					vanilla,
					attachments
			);
			program.link(vanilla, mapper, struct);
			for (ShaderAttachment attachment : attachments) attachment.delete();
			
			// log error
			program.validate("Optifine:Base");
			
			program.setFirstBind(() -> firstBind(vanilla));
			setupProg(program, vanilla);
		}
//		if (vanillaShadow != null) {
//			List<ShaderAttachment> attachments = new ArrayList<>();
//			for (AttachmentSpecifier specifier : specifiersShadow) {
//				if (specifier == null) continue;
//
//				TemplateTransformation apply = struct.getTransformation(
//						specifier.type.strName(), transformers, transformations
//				);
//				ShaderAttachment attachment = new ShaderAttachment(
//						specifier.source, specifier.type,
//						apply, vanillaShadow,
//						specifier.preprocess, mapper,
//						processor, struct.location,
//						"iris/shadow/"
//				);
//				attachments.add(attachment);
//
//				String srcFl = specifier.fileName + "." + specifier.type.strName();
//				sourceNames.add(srcFl);
//			}
//
//			// make program
//			TemplatedProgram programShadow = new TemplatedProgram(
//					vanillaShadow,
//					attachments
//			);
//			programShadow.link(vanillaShadow, mapper, struct);
//			for (ShaderAttachment attachment : attachments) attachment.delete();
//
//			// log error
//			programShadow.validate("Iris/Oculus:Shadow");
//			this.programShadow = programShadow;
//
//			programShadow.setFirstBind(() -> firstBind(vanillaShadow));
//			setupProg(programShadow, vanillaShadow);
//		} else {
//			programShadow = BlackHoleProgram.INSTANCE;
//		}
	}
	
	private void setupProg(OFTemplatedProgram program, Program vanilla) {
		Map<CachedUniform, Integer> uniformModCounts = new HashMap<>();
		// caches parent value so that it can be reset
		Map<IPaCoPainReducer, Object> uniformValueCache = new HashMap<>();
		long[] lastTick = new long[2];
		int[] lastFrame = new int[2];
		
		Map<IPaCoPainReducer, Object> parCache = new HashMap<>();
		Map<IPaCoPainReducer, Object> selfCache = new HashMap<>();
		Map<IPaCoPainReducer, Integer> uniformIds = new HashMap<>();
		
		program.setPreBind(() -> preBind(program.id, vanilla, uniformModCounts, uniformValueCache, lastTick, lastFrame, parCache, selfCache, uniformIds));
		program.setPostClear(() -> postClear(vanilla, uniformModCounts, uniformValueCache, lastTick, lastFrame, parCache, selfCache, uniformIds));
	}
	
	private void firstBind(Program vanilla) {
		FIRST_BIND = true;
	}
	
	private void preBind(
			int progId, Program vanilla, Map<CachedUniform, Integer> uniformModCounts,
			Map<IPaCoPainReducer, Object> uniformValueCache, long[] lastTick, int[] lastFrame,
			Map<IPaCoPainReducer, Object> parCache, Map<IPaCoPainReducer, Object> selfCache,
			Map<IPaCoPainReducer, Integer> uniformIds
	) {
//		System.out.println("PRE-BIND");
		CustomUniforms uniforms = OptifineAccessor.getCustomUniforms();
		ShaderUniforms progUniforms = OptifineAccessor.getShaderUniforms();
		
		PaCoOFUniformListable mtPU = ((PaCoOFUniformListable) progUniforms);
		
		if (FIRST_BIND) {
			for (ShaderUniformBase uniformB : mtPU.getUforms()) {
				IPaCoPainReducer uniform = (IPaCoPainReducer) uniformB;
				int id = GL20.glGetUniformLocation(progId, uniformB.getName());
				System.out.println(uniformB.getName() + " " + uniformB.getLocation() + "->" + id);
				uniformIds.put(uniform, id);

				try {
					uniformValueCache.put(uniform, uniform.getCachedValue());
					uniform.setCachedValue(null);
					((ILocationedObject) uniform).pandoraCore$virtualLocation(id);
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		} else {
			for (ShaderUniformBase uniformB : mtPU.getUforms()) {
//				System.out.println(uniformB.getName());
				IPaCoPainReducer uniform = (IPaCoPainReducer) uniformB;
				int id = uniformIds.get(uniform);

				uniformValueCache.put(uniform, uniform.getCachedValue());
				uniform.setCachedValue(selfCache.get(uniform));
				((ILocationedObject) uniform).pandoraCore$virtualLocation(id);
			}
		}
		
//		if (uniforms != null) {
//			uniforms.update();
//		}
		
		FIRST_BIND = false;
	}
	
	private void postClear(
			Program vanilla, Map<CachedUniform, Integer> uniformModCounts,
			Map<IPaCoPainReducer, Object> uniformValueCache, long[] lastTick, int[] lastFrame,
			Map<IPaCoPainReducer, Object> parCache, Map<IPaCoPainReducer, Object> selfCache,
			Map<IPaCoPainReducer, Integer> uniformIds
	) {
		CustomUniforms uniforms = OptifineAccessor.getCustomUniforms();
		ShaderUniforms progUniforms = OptifineAccessor.getShaderUniforms();
		
		PaCoOFUniformListable mtPU = ((PaCoOFUniformListable) progUniforms);
		
		for (ShaderUniformBase uniformB : mtPU.getUforms()) {
			IPaCoPainReducer uniform = (IPaCoPainReducer) uniformB;
			selfCache.put(uniform, uniform.getCachedValue());
			Object o = parCache.getOrDefault(uniform, null);
			uniform.setCachedValue(o);
			((ILocationedObject) uniform).pandoraCore$virtualLocation(-1);
		}
	}
	
	public static boolean isFirstBind() {
		return FIRST_BIND;
	}
	
	public static void setBound() {
		FIRST_BIND = false;
	}
	
	@Override
	public void apply() {
		try {
//			if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
//				programShadow.bind();
//			} else {
			program.bind();
//			}
		} catch (Throwable err) {
			err.printStackTrace();
		}
//		VoidShader.INSTANCE.apply();
	}
	
	@Override
	public void upload() {
//		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
//			programShadow.upload();
//		} else {
//			program.upload();
//		}
		VoidShader.INSTANCE.upload();
	}
	
	@Override
	public void destroy() {
		program.close();
//		programShadow.close();
	}
	
	@Override
	public void clear() {
//		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
//			programShadow.clear();
//		} else {
		program.clear();
//		}
		super.clear();
//		VoidShader.INSTANCE.clear();
	}
	
	public boolean matches(String mod, String active) {
		String cat = mod + ":" + active;
		for (String sourceName : sourceNames) {
			return sourceName.equals(cat);
		}
		return false;
	}
	
	@Override
	public AbstractUniform getUniform(String name, int type, int count) {
//		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
//			return programShadow.getUniform(name, type, count);
//		} else {
//			return program.getUniform(name, type, count);
//		}
		return VoidShader.INSTANCE.getUniform(name, type, count);
	}
	
	@Override
	public int getAttributeLocation(String name) {
//		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
//			return programShadow.getAttributeLocation(name);
//		} else {
		return program.getAttributeLocation(name);
//		}
//		return -1;
	}
	
	@Override
	public boolean isVanilla() {
		return false;
	}
}

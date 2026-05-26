package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl;

import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.BlackHoleProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.TemplatedProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTrackerListable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.*;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IrisTemplatedShader extends TemplatedShader {
	protected static boolean FIRST_BIND = false;
	
	TemplatedProgram program;
	BaseProgram programShadow;
	List<String> sourceNames = new ArrayList<>();
	
	public IrisTemplatedShader(
			VariableMapper mapper,
			TemplateLoader loader,
			Map<String, String> transformers,
			Function<String, TemplateTransformation> transformations,
			TemplateShaderResourceLoader.TemplateStruct struct,
			TransformationProcessor processor,
			String template, ShaderInstance vanilla, AttachmentSpecifier[] specifiers,
			String templateShadow, ShaderInstance vanillaShadow, AttachmentSpecifier[] specifiersShadow
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
						apply, vanilla,
						specifier.preprocess, mapper,
						processor, struct.location,
						"iris/main/"
				);
				attachments.add(attachment);
				
				String srcFl = specifier.fileName + "." + specifier.type.strName();
				sourceNames.add(srcFl);
			}
			
			// make program
			program = new TemplatedProgram(
					vanilla,
					attachments
			);
			program.link(vanilla, mapper, struct);
			for (ShaderAttachment attachment : attachments) attachment.delete();
			
			// log error
			program.validate("Iris/Oculus:Base");
			
			program.setFirstBind(() -> firstBind(vanilla));
			setupProg(program, vanilla);
		}
		if (vanillaShadow != null) {
			List<ShaderAttachment> attachments = new ArrayList<>();
			for (AttachmentSpecifier specifier : specifiersShadow) {
				if (specifier == null) continue;
				
				TemplateTransformation apply = struct.getTransformation(
						specifier.type.strName(), transformers, transformations
				);
				ShaderAttachment attachment = new ShaderAttachment(
						specifier.source, specifier.type,
						apply, vanillaShadow,
						specifier.preprocess, mapper,
						processor, struct.location,
						"iris/shadow/"
				);
				attachments.add(attachment);
				
				String srcFl = specifier.fileName + "." + specifier.type.strName();
				sourceNames.add(srcFl);
			}
			
			// make program
			TemplatedProgram programShadow = new TemplatedProgram(
					vanillaShadow,
					attachments
			);
			programShadow.link(vanillaShadow, mapper, struct);
			for (ShaderAttachment attachment : attachments) attachment.delete();
			
			// log error
			programShadow.validate("Iris/Oculus:Shadow");
			this.programShadow = programShadow;
			
			programShadow.setFirstBind(() -> firstBind(vanillaShadow));
			setupProg(programShadow, vanillaShadow);
		} else {
			programShadow = BlackHoleProgram.INSTANCE;
		}
	}
	
	private void setupProg(TemplatedProgram program, ShaderInstance vanilla) {
		Map<CachedUniform, Integer> uniformModCounts = new HashMap<>();
		// caches parent value so that it can be reset
		Map<Uniform, Object> uniformValueCache = new HashMap<>();
		long[] lastTick = new long[2];
		int[] lastFrame = new int[2];
		
		Map<Uniform, Object> parCache = new HashMap<>();
		Map<Uniform, Object> selfCache = new HashMap<>();
		
		program.setPreBind(() -> preBind(vanilla, uniformModCounts, uniformValueCache, lastTick, lastFrame, parCache, selfCache));
		program.setPostClear(() -> postClear(vanilla, uniformModCounts, uniformValueCache, lastTick, lastFrame, parCache, selfCache));
	}
	
	private void firstBind(ShaderInstance vanilla) {
		FIRST_BIND = true;
	}
	
	ImmutableList<Uniform> cachedInit;
	List<GlUniform1iCall> initSamplers;
	List<GlUniform1iCall> initImages;
	
	private void preBind(
			ShaderInstance vanilla, Map<CachedUniform, Integer> uniformModCounts,
			Map<Uniform, Object> uniformValueCache, long[] lastTick, int[] lastFrame,
			Map<Uniform, Object> parCache, Map<Uniform, Object> selfCache
	) {
		ExtendedShader ext = (ExtendedShader) vanilla;
		
		CustomUniforms uniforms = ((IPacoCustomUniformAccessor) ext).pandoraCore$getCustomUniforms();
		
		ProgramUniforms progUniforms = ((IPacoAccessInitializables) ext).pandoraCore$getUniforms();
		IPacoInitCachable<ImmutableList<Uniform>> accessor = (IPacoInitCachable<ImmutableList<Uniform>>) progUniforms;
		
		IPaCoFrameTickAccessor ft = (IPaCoFrameTickAccessor) progUniforms;
		lastTick[0] = ft.getLastTick();
		lastFrame[0] = ft.getLastFrame();
		
		{
			for (Uniform uniform : ft.getDynamic()) {
				IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
				parCache.put(uniform, reducer.getCachedValue());
				
				Object o = selfCache.getOrDefault(uniform, null);
				reducer.setCachedValue(o);
			}
			// TODO: call the method from iris code for consistency
			long tick = ft.accessGetCurrentTick();
			if (lastTick[1] != tick) {
				ft.setLastTick(tick);
				
				for (Uniform uniform : ft.getPerTick()) {
					IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
					parCache.put(uniform, reducer.getCachedValue());
					
					Object o = selfCache.getOrDefault(uniform, null);
					reducer.setCachedValue(o);
				}
			}
			
			int frame = SystemTimeUniforms.COUNTER.getAsInt();
			if (lastFrame[1] != frame) {
				ft.setLastFrame(frame);
				
				for (Uniform uniform : ft.getPerFrame()) {
					IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
					parCache.put(uniform, reducer.getCachedValue());
					
					Object o = selfCache.getOrDefault(uniform, null);
					reducer.setCachedValue(o);
				}
			}
		}
		
		IPacoInitCachable<List<GlUniform1iCall>> samplers = (IPacoInitCachable<List<GlUniform1iCall>>) ((IPacoAccessInitializables) ext).pandoraCore$getSamplers();
		IPacoInitCachable<List<GlUniform1iCall>> images = (IPacoInitCachable<List<GlUniform1iCall>>) ((IPacoAccessInitializables) ext).pandoraCore$getImages();
		
		cachedInit = accessor.pandoraCore$getCurrentInitializer();
		initSamplers = samplers.pandoraCore$getCurrentInitializer();
		initImages = images.pandoraCore$getCurrentInitializer();
		
		uniformValueCache.clear();
		
		if (FIRST_BIND) {
			accessor.pandoraCore$setInitializer(accessor.pandoraCore$getInitializer());
			
			for (Uniform uniform : accessor.pandoraCore$getInitializer()) {
				try {
					uniformValueCache.put(uniform, ((IPaCoPainReducer) uniform).getCachedValue());
					((IPaCoPainReducer) uniform).setCachedValue(null);
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		} else {
			// avoid repeating
			accessor.pandoraCore$setInitializer(null);
			samplers.pandoraCore$setInitializer(null);
			images.pandoraCore$setInitializer(null);
		}
		
		for (IPaCoModTracker mt : ((IPaCoModTrackerListable) uniforms).pandoraCore$listTrackers()) {
			mt.pandoraCore$isolate();
			
			CachedUniform uniform = (CachedUniform) mt;
			Integer i = uniformModCounts.getOrDefault(uniform, null);
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

//		System.out.println(ext);
	}
	
	private void postClear(
			ShaderInstance vanilla, Map<CachedUniform, Integer> uniformModCounts,
			Map<Uniform, Object> uniformValueCache, long[] lastTick, int[] lastFrame,
			Map<Uniform, Object> parCache, Map<Uniform, Object> selfCache
	) {
		ExtendedShader ext = (ExtendedShader) vanilla;
		ProgramUniforms progUniforms = ((IPacoAccessInitializables) ext).pandoraCore$getUniforms();
		IPacoInitCachable<ImmutableList<Uniform>> accessor = (IPacoInitCachable<ImmutableList<Uniform>>) progUniforms;
		IPacoInitCachable<List<GlUniform1iCall>> samplers = (IPacoInitCachable<List<GlUniform1iCall>>) ((IPacoAccessInitializables) ext).pandoraCore$getSamplers();
		IPacoInitCachable<List<GlUniform1iCall>> images = (IPacoInitCachable<List<GlUniform1iCall>>) ((IPacoAccessInitializables) ext).pandoraCore$getImages();
		
		accessor.pandoraCore$setInitializer(cachedInit);
		samplers.pandoraCore$setInitializer(initSamplers);
		images.pandoraCore$setInitializer(initImages);
		
		CustomUniforms uniforms = ((IPacoCustomUniformAccessor) ext).pandoraCore$getCustomUniforms();
		
		for (IPaCoModTracker mt : ((IPaCoModTrackerListable) uniforms).pandoraCore$listTrackers()) {
			mt.pandoraCore$release();
		}
		
		uniformValueCache.forEach((k, v) -> ((IPaCoPainReducer) k).setCachedValue(v));
		
		IPaCoFrameTickAccessor ft = (IPaCoFrameTickAccessor) progUniforms;
		lastTick[1] = ft.getLastTick();
		lastFrame[1] = ft.getLastFrame();
		ft.setLastTick(lastTick[0]);
		ft.setLastFrame(lastFrame[0]);
		
		{
			for (Uniform uniform : ft.getDynamic()) {
				IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
				selfCache.put(uniform, reducer.getCachedValue());
				
				Object o = parCache.getOrDefault(uniform, null);
				reducer.setCachedValue(o);
			}
			for (Uniform uniform : ft.getPerFrame()) {
				IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
				selfCache.put(uniform, reducer.getCachedValue());
				
				Object o = parCache.getOrDefault(uniform, null);
				reducer.setCachedValue(o);
			}
			for (Uniform uniform : ft.getPerTick()) {
				IPaCoPainReducer reducer = (IPaCoPainReducer) uniform;
				selfCache.put(uniform, reducer.getCachedValue());
				
				Object o = parCache.getOrDefault(uniform, null);
				reducer.setCachedValue(o);
			}
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
			if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
				programShadow.bind();
			} else {
				program.bind();
			}
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
	
	@Override
	public void upload() {
		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
			programShadow.upload();
		} else {
			program.upload();
		}
	}
	
	@Override
	public void destroy() {
		program.close();
		programShadow.close();
	}
	
	@Override
	public void clear() {
		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
			programShadow.clear();
		} else {
			program.clear();
		}
		super.clear();
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
		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
			return programShadow.getUniform(name, type, count);
		} else {
			return program.getUniform(name, type, count);
		}
	}
	
	@Override
	public int getAttributeLocation(String name) {
		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
			return programShadow.getAttributeLocation(name);
		} else {
			return program.getAttributeLocation(name);
		}
	}
	
	@Override
	public boolean isVanilla() {
		return false;
	}
}

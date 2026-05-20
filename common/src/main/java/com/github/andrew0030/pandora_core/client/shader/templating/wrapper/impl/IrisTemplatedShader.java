package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.BlackHoleProgram;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.TemplatedProgram;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTrackerListable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoAccessInitializables;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoCustomUniformAccessor;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoUniformInitalizerAccessor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.shadows.ShadowRenderingState;
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
                        processor, struct.location
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
	        program.setPreBind(() -> preBind(vanilla));
	        program.setPostClear(() -> postClear(vanilla));
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
                        processor, struct.location
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
            programShadow.setPreBind(() -> preBind(vanillaShadow));
            programShadow.setPostClear(() -> postClear(vanillaShadow));
        } else {
            programShadow = BlackHoleProgram.INSTANCE;
        }
    }
	
	private void firstBind(ShaderInstance vanilla) {
		FIRST_BIND = true;
	}
	
	private Map<CachedUniform, Integer> uniformModCounts = new HashMap<>();
	ImmutableList<Uniform> cachedInit;
	
	private void postClear(ShaderInstance vanilla) {
		ExtendedShader ext = (ExtendedShader) vanilla;
		ProgramUniforms progUniforms = ((IPacoAccessInitializables)ext).pandoraCore$getUniforms();
		IPacoUniformInitalizerAccessor accessor = (IPacoUniformInitalizerAccessor) progUniforms;
		accessor.pandoraCore$setInitializer(cachedInit);
	}
	
	private void preBind(ShaderInstance vanilla) {
		ExtendedShader ext = (ExtendedShader) vanilla;
		
		CustomUniforms uniforms = ((IPacoCustomUniformAccessor) ext).pandoraCore$getCustomUniforms();
		
		ProgramUniforms progUniforms = ((IPacoAccessInitializables)ext).pandoraCore$getUniforms();
		IPacoUniformInitalizerAccessor accessor = (IPacoUniformInitalizerAccessor) progUniforms;
		cachedInit = accessor.pandoraCore$getInitializer();
		accessor.pandoraCore$setInitializer(accessor.pandoraCore$getCachedInitializer());
		
		for (IPaCoModTracker mt : ((IPaCoModTrackerListable) uniforms).pandoraCore$listTrackers()) {
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
}

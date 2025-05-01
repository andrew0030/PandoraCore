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
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IrisTemplatedShader extends TemplatedShader {
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
        } else {
            programShadow = BlackHoleProgram.INSTANCE;
        }
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
}

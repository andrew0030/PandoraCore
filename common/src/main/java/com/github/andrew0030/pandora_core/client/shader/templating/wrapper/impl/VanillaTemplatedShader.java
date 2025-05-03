package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.TemplatedProgram;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.ShaderAttachment;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class VanillaTemplatedShader extends TemplatedShader {
    //    ShaderInstance shadow;
    TemplatedProgram program;
    //    int idShadow;
//    String vshName, fshName;
    List<String> sourceNames = new ArrayList<>();

    public VanillaTemplatedShader(
            VariableMapper mapper,
            TemplateLoader loader,
            Map<String, String> transformers,
            Function<String, TemplateTransformation> transformations,
            TemplateShaderResourceLoader.TemplateStruct struct,
            TransformationProcessor processor,
            String template,
            ShaderInstance vanilla,
            AttachmentSpecifier[] specifiers
    ) {
        super(loader, struct, template);

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
        program.validate("Vanilla");
    }

    @Override
    public void apply() {
        try {
//            if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            program.bind();
//            } else {
//                RenderSystem.setShader(() -> shadow);
//                ((IPaCoConditionallyBindable) shadow).pandoraCore$disableBind();
//                GL20.glUseProgram(idShadow);
//            }
        } catch (Throwable err) {
        }
    }

    @Override
    public void upload() {
        program.upload();
    }

    @Override
    public void destroy() {
        program.close();
//        GL20.glDeleteProgram(idShadow);
    }

    @Override
    public void clear() {
        program.clear();
//        ((IPaCoConditionallyBindable) shadow).pandoraCore$enableBind();
//        shadow.clear();
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
        return program.getUniform(name, type, count);
    }

    @Override
    public int getAttributeLocation(String name) {
        return program.getAttributeLocation(name);
    }
}

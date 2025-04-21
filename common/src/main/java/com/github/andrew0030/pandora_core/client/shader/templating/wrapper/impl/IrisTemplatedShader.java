package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader.ShaderAttachment;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.loader.TemplatedProgram;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoAccessibleProgram;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.ShaderInstance;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IrisTemplatedShader extends TemplatedShader {
//    ShaderInstance shadow;
    TemplatedProgram program;
//    int idShadow;
//    String vshName, fshName;
    List<String> sourceNames = new ArrayList<>();

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Templated Vanilla/Iris");

    public IrisTemplatedShader(
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
                    processor
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
        program.validate("Iris/Oculus");
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
}

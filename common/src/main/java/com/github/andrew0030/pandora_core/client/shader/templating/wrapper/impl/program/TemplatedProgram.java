package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.ShaderAttachment;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
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
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader.ABSTRACT_INST;

public class TemplatedProgram extends BaseProgram {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Templated Vanilla/Iris");

    ShaderInstance from;
    int id;

    public TemplatedProgram(ShaderInstance from, List<ShaderAttachment> attachments) {
        this.from = from;

        id = GL20.glCreateProgram();

        for (ShaderAttachment attachment : attachments) {
            GL20.glAttachShader(id, attachment.id);
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
        TemplatedShader.bindAttributes(id, index, transformation);
        GL20.glLinkProgram(id);
    }

    public void bind() {
        RenderSystem.setShader(() -> from);
        ((IPaCoConditionallyBindable) from).pandoraCore$disableBind();
        GL20.glUseProgram(id);
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
}

package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
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
import java.util.Map;

public class IrisTemplatedShader extends TemplatedShader {
    ShaderInstance vanilla;
    ShaderInstance shadow;
    int id;
    int idShadow;
    String vshName, fshName;


    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Templated Vanilla/Iris");

    public IrisTemplatedShader(
            TemplateLoader loader,
            VariableMapper mapper,
            TemplateShaderResourceLoader.TemplateStruct transformation,
            String template,
            ShaderInstance vanilla,
            String vsh, String fsh, String gsh,
            String vshName, String fshName, String gshName,
            TransformationProcessor processor
    ) {
        super(loader, transformation, template);
        this.vanilla = vanilla;

        this.vshName = vshName + ".vsh";
        this.fshName = fshName + ".fsh";
        // TODO: gsh, tesse, tessc

        id = GL20.glCreateProgram();
        // load transformed vertex shader
        int vertId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        {
            GL20.glShaderSource(vertId, vsh);
            GL20.glCompileShader(vertId);
            if (GlStateManager.glGetShaderi(vertId, 35713) == 0) {
                String $$7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(vertId, 32768));
                GL20.glDeleteShader(vertId);
                GL20.glDeleteProgram(id);
                try {
                    throw new IOException("Couldn't compile " + transformation.location + " from " + vanilla.getName() + " program (" + vanilla.getName() + ", " + transformation.location + ") : " + $$7);
                } catch (Throwable err) {
                    throw new RuntimeException(err);
                }
            }
        }

        int index = 0;
        for (String elementAttributeName : vanilla.getVertexFormat().getElementAttributeNames()) {
            Uniform.glBindAttribLocation(id, index++, mapper.mapTo(null, elementAttributeName));
        }
        TemplatedShader.bindAttributes(id, index, transformation);

        int fragId = ((IPaCoAccessibleProgram) vanilla.getFragmentProgram()).pandoraCore$getId();
        // bind shaders
        GL20.glAttachShader(id, vertId);
        GL20.glAttachShader(id, fragId);
        GL20.glLinkProgram(id);
        // delete vertex shader as it is now bound and not necessary

        // log error
        GL20.glDeleteShader(vertId);
        int i = GlStateManager.glGetProgrami(id, 35714);
        if (i == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", vshName, fshName);
            LOGGER.warn(GlStateManager.glGetProgramInfoLog(id, 32768));
        }
    }

    @Override
    public void apply() {
        try {
            if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                RenderSystem.setShader(() -> vanilla);
                ((IPaCoConditionallyBindable) vanilla).pandoraCore$disableBind();
                GL20.glUseProgram(id);
            } else {
                RenderSystem.setShader(() -> shadow);
                ((IPaCoConditionallyBindable) shadow).pandoraCore$disableBind();
                GL20.glUseProgram(idShadow);
            }
        } catch (Throwable err) {
        }
    }

    @Override
    public void upload() {
        try {
            for (Uniform pacoUform : pacoUforms) {
                pacoUform.upload();
            }
        } catch (Throwable err) {
        }
    }

    @Override
    public void destroy() {
        for (Uniform pacoUform : pacoUforms) {
            pacoUform.close();
        }
        GL20.glDeleteProgram(id);
        GL20.glDeleteProgram(idShadow);
    }

    @Override
    public void clear() {
        ((IPaCoConditionallyBindable) vanilla).pandoraCore$enableBind();
        vanilla.clear();
        ((IPaCoConditionallyBindable) shadow).pandoraCore$enableBind();
        shadow.clear();
        super.clear();
    }

    public boolean matches(String mod, String active) {
        return vshName.equals(mod + ":" + active) || fshName.equals(mod + ":" + active);
    }

    Map<String, AbstractUniform> uniforms = new Object2ObjectRBTreeMap<>();
    ArrayList<Uniform> pacoUforms = new ArrayList<>();

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        AbstractUniform paco = uniforms.get(name);
        if (paco != null) {
            return paco;
        }

        Uniform uForm = vanilla.getUniform(name);
        if (uForm != null) uniforms.put(name, uForm);
        else {
            int loc = GL20.glGetUniformLocation(id, name);
            if (loc != -1) {
                uForm = new Uniform(name, type, count, vanilla);
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
}

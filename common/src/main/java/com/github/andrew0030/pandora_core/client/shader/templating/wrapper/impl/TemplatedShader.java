package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL20;

public abstract class TemplatedShader {
    protected final TemplateLoader loader;
    protected final TemplateShaderResourceLoader.TemplateStruct transformation;
    protected final String template;

    public TemplatedShader(
            TemplateLoader loader,
            TemplateShaderResourceLoader.TemplateStruct transformation,
            String template
    ) {
        this.loader = loader;
        this.transformation = transformation;
        this.template = template;
    }

    public abstract void apply();

    public abstract void upload();

    public abstract void destroy();

    public ResourceLocation location() {
        return transformation.location;
    }

    public TemplateShaderResourceLoader.TemplateStruct transformation() {
        return transformation;
    }

    public void clear() {
        GL20.glUseProgram(0);
    }

    public TemplateLoader getLoader() {
        return loader;
    }

    public abstract AbstractUniform getUniform(String name, int type, int count);

    protected static final AbstractUniform ABSTRACT_INST = new AbstractUniform();
}

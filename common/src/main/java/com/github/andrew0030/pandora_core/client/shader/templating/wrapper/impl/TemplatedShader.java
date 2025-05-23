package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.TemplatedProgram;
import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.util.HashMap;
import java.util.Map;

public abstract class TemplatedShader {
    protected final TemplateLoader loader;
    protected final TemplateShaderResourceLoader.TemplateStruct transformation;
    protected final String template;
    private Map<String, Integer> attributeLocations = new HashMap<>();

    public TemplatedShader(
            TemplateLoader loader,
            TemplateShaderResourceLoader.TemplateStruct transformation,
            String template
    ) {
        this.loader = loader;
        this.transformation = transformation;
        this.template = template;
    }

    public static void bindAttributes(TemplatedProgram shader, int id, int index, TemplateShaderResourceLoader.TemplateStruct transformation) {
        for (String vertexAttribute : transformation.getInstanceData()) {
            int aid = GL32.glGetAttribLocation(id, vertexAttribute);
            shader.attributeLocations.put(vertexAttribute, aid);
        }
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

    public static final AbstractUniform ABSTRACT_INST = new AbstractUniform();

    public boolean hasDirect() {
        return true;
    }

    public abstract int getAttributeLocation(String name);
}

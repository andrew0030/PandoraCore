package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.mojang.blaze3d.shaders.AbstractUniform;
import org.jetbrains.annotations.ApiStatus;

public class TemplatedShaderInstance {
    TemplatedShader shader;
    TemplateShaderResourceLoader.TemplateStruct transformation;

    public TemplatedShaderInstance(TemplatedShader shader, TemplateShaderResourceLoader.TemplateStruct transformation) {
        this.shader = shader;
        this.transformation = transformation;
    }

    public void apply() {
        shader.apply();
        shader.upload();
    }

    /**
     * Only for internal use purposes
     * Do not call
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public TemplatedShader getDirect() {
        return shader;
    }

    /**
     * Only for internal use purposes
     * Do not call
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = true)
    public void updateDirect(TemplatedShader shader) {
        this.shader = shader;
    }

    public void clear() {
        shader.clear();
    }

    public AbstractUniform getUniform(String name, int type, int count) {
        return shader.getUniform(name, type, count);
    }

    public boolean hasDirect() {
        return shader.hasDirect();
    }
}

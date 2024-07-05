package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import net.minecraft.client.renderer.RenderStateShard;
import org.jetbrains.annotations.ApiStatus;

public class TemplatedShaderInstance {
    TemplatedShader shader;
    TemplateTransformation transformation;

    public TemplatedShaderInstance(TemplatedShader shader, TemplateTransformation transformation) {
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
}

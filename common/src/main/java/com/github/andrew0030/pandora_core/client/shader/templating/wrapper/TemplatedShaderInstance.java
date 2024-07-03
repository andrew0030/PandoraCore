package com.github.andrew0030.pandora_core.client.shader.templating.wrapper;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import net.minecraft.client.renderer.RenderStateShard;

public class TemplatedShaderInstance {
    TemplatedShader shader;
    TemplateTransformation transformation;

    public void apply() {
        shader.apply();
        shader.upload();
    }
}

package com.github.andrew0030.pandora_core.client.utils.shader;

import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

public class PaCoShaderStateShard extends RenderStateShard.ShaderStateShard {
    Runnable setup;
    Runnable clear;

    // only useful for immediate use render types
    public PaCoShaderStateShard(TemplatedShader shader) {
        super();
        setup = () -> {
            shader.apply();
            shader.upload();
        };
        clear = shader::clear;
    }

    public PaCoShaderStateShard(ShaderWrapper shaderWrapper) {
        super();
        setup = () -> {
            shaderWrapper.apply();
            shaderWrapper.upload();
        };
        clear = shaderWrapper::clear;
    }

    // might as well
    public PaCoShaderStateShard(ShaderInstance shaderInstance) {
        super();
        setup = () -> shaderInstance.apply();
        clear = shaderInstance::clear;
    }

    @Override
    public void setupRenderState() {
        setup.run();
    }

    @Override
    public void clearRenderState() {
        clear.run();
    }
}

package com.github.andrew0030.pandora_core.client.utils.shader;

import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

public class PaCoShaderStateShard extends RenderStateShard.ShaderStateShard {
    Runnable setup;
    Runnable clear;

    public PaCoShaderStateShard(TemplatedShaderInstance shaderInstance) {
        super();
        setup = () -> {
            shaderInstance.apply();
        };
        clear = () -> {
            shaderInstance.clear();
        };
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

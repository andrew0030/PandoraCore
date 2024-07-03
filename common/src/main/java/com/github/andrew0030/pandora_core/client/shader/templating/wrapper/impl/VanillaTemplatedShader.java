package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import net.minecraft.client.renderer.ShaderInstance;

public class VanillaTemplatedShader extends TemplatedShader {
    ShaderInstance vanilla;

    @Override
    public void apply() {
        // TODO
    }

    @Override
    public void upload() {
        // TODO: setup a mixin to allow disabling the line that enables the program
        vanilla.apply();
    }
}

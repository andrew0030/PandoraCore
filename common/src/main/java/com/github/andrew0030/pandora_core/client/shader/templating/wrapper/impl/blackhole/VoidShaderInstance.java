package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.blackhole;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.BaseProgram;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.BlackHoleProgram;
import com.mojang.blaze3d.shaders.AbstractUniform;

public class VoidShaderInstance extends TemplatedShaderInstance {
    BaseProgram program = BlackHoleProgram.INSTANCE;
    public static TemplatedShaderInstance INSTANCE = new VoidShaderInstance(
            VoidShader.INSTANCE, null
    );

    private VoidShaderInstance(TemplatedShader shader, TemplateShaderResourceLoader.TemplateStruct transformation) {
        super(shader, transformation);
    }

    @Override
    public void apply() {
        program.bind();
    }

    @Override
    public void clear() {
        program.clear();
    }

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        return program.getUniform(name, type, count);
    }
}

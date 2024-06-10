package com.github.andrew0030.pandora_core.client.shader.holder;

import com.mojang.blaze3d.shaders.AbstractUniform;

import java.util.List;
import java.util.function.Supplier;

public class PaCoUniformHolder implements Supplier<PaCoUniformHolder.UniformSetter> {
    UniformSetter value = new UniformSetter();
    boolean isDirty = true;

    @Override
    public UniformSetter get() {
        return value;
    }

    public class UniformSetter {
        List<AbstractUniform> uniforms;

        public void set(float value) {
            for (AbstractUniform uniform : uniforms) {
                uniform.set(value);
            }
        }
    }
}

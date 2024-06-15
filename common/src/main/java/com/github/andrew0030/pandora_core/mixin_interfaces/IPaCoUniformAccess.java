package com.github.andrew0030.pandora_core.mixin_interfaces;

import com.mojang.blaze3d.shaders.AbstractUniform;

import java.util.List;

public interface IPaCoUniformAccess {
    void pandoraCore$setUniform(String key, float value);
    List<AbstractUniform> pandoraCore$getUniforms(String key);
}
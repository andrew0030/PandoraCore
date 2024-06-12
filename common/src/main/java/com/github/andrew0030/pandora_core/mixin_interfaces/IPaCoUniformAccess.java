package com.github.andrew0030.pandora_core.mixin_interfaces;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.PostPass;

import java.util.List;

public interface IPaCoUniformAccess {
    void setPaCoUniform(String key, float value);
    List<AbstractUniform> getPaCoUniforms(String key);
}

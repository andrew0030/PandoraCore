package com.github.andrew0030.pandora_core.mixin_interfaces;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.PostChain;

import java.util.List;

public interface IPaCoUniformAccess {
    /** Will be removed in <strong>1.20.5</strong> and onwards, as vanilla added its own setUniform method to {@link PostChain}. */
    @Deprecated
    void pandoraCore$setUniform(String key, float value);
    List<AbstractUniform> pandoraCore$getUniforms(String key);
}
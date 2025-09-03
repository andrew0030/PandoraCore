package com.github.andrew0030.pandora_core.mixin.shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.mojang.blaze3d.shaders.AbstractUniform;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractUniform.class)
public class AbstractUniformMixin implements IPacoDirtyable {
    @Override
    public void pandoraCore$markDirty() {
        throw new RuntimeException("huh.");
    }
}

package com.github.andrew0030.pandora_core.mixin.shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Uniform.class)
public abstract class UniformMixin implements IPacoDirtyable {
    @Shadow protected abstract void markDirty();

    @Override
    public void pandoraCore$markDirty() {
        markDirty();
    }
}

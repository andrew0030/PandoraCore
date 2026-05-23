package com.github.andrew0030.pandora_core.mixin.render;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Window.class, priority = Integer.MIN_VALUE)
public class WindowMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 2, ordinal = 0), require = 0)
    public int preCreate(int constant) {
        if (constant < 3) constant = 3;
        return constant;
    }
}
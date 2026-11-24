package com.github.andrew0030.pandora_core.mixin.fixes;

import net.minecraft.client.renderer.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/*
 * Authors: GiantLuigi4, andrew0030
 *
 * Fixes the ResourceLocations of post shaders which include a mod id, in a non-destructive way.
 *
 * NOTE: Normally this isn't needed because Forge patches this, but OptiFine has a poorly written patch that
 * attempts to preserve Forge's fix, but fails to do so. So in order to restore the functionality, this mixin
 * gets loaded when OptiFine is installed.
 */
@Mixin(value = EffectInstance.class, priority = Integer.MAX_VALUE)
public class EffectInstanceMixin {

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V", ordinal = 0),
            require = 0
    )
    public String modifyRL0(String src) {
        return fix(src);
    }

    @ModifyArg(
            method = "getOrCreate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V", ordinal = 0),
            require = 0
    )
    private static String modifyRL1(String src) {
        return fix(src);
    }

    @Unique
    private static String fix(String src) {
        if (!src.startsWith("shaders/program/")) return src; // If the String starts with a mod id, we return.
        int colonIndex = src.indexOf(':'); // Using indexOf as a more efficient alternative to split.
        if (colonIndex == -1) return src; // If there is no ":" in the String, we return.

        String left = src.substring("shaders/program/".length(), colonIndex);
        String right = src.substring(colonIndex + 1);
        return left + ":shaders/program/" + right;
    }
}
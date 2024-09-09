package com.github.andrew0030.pandora_core.mixin.fixes;

import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/* Authors: GiantLuigi4, andrew0030 */
/* For the targets, we took reference from: https://github.com/Fabricators-of-Create/Porting-Lib/blob/1.18.2/src/main/java/io/github/fabricators_of_create/porting_lib/mixin/client/ShaderInstanceMixin.java */
/* Fixes the ResourceLocations of core shaders which include a mod id, in a non-destructive way. */
/* This is technically now part of FAPI, but we decided to keep this mixin around as a QoL feature, and thanks to its non-destructive nature. */
@Mixin(value = ShaderInstance.class, priority = Integer.MAX_VALUE)
public class ShaderInstanceMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V", ordinal = 0),
            require = 0
    )
    public String modifyRL0(String src) {
        return fix(src);
    }

    @ModifyVariable(
            method = "getOrCreate",
            at = @At(value = "STORE"),
            ordinal = 1,
            require = 0
    )
    private static String modifyRL1(String src) {
        return fix(src);
    }

    @Unique
    private static String fix(String src) {
        if (!src.startsWith("shaders/core/")) return src; // If the String starts with a mod id, we return.
        int colonIndex = src.indexOf(':'); // Using indexOf as a more efficient alternative to split.
        if (colonIndex == -1) return src; // If there is no ":" in the String, we return.

        String left = src.substring("shaders/core/".length(), colonIndex);
        String right = src.substring(colonIndex + 1);
        return left + ":shaders/core/" + right;
    }
}
package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;nglShaderSource(IIJJ)V"), method = "glShaderSource")
    private static void preShaderSource(int shader, List<String> shaderData, CallbackInfo ci) {
        IrisTemplateLoader.shaderSource(shaderData);
    }
}
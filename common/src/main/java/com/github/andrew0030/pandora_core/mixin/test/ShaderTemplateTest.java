package com.github.andrew0030.pandora_core.mixin.test;

import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 10001)
public class ShaderTemplateTest {
    @Inject(at = @At(value = "TAIL"), method = "renderChunkLayer")
    public void preDrawEnts(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (renderType == RenderType.solid()) {
            TemplateShaderTest.draw(
                    poseStack,
                    camX,
                    camY,
                    camZ
            );
            renderType.setupRenderState();
        }
    }
}
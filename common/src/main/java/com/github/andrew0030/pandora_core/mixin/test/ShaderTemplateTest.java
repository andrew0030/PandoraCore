package com.github.andrew0030.pandora_core.mixin.test;

import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 10001)
public class ShaderTemplateTest {
    @Inject(at = @At(value = "TAIL"), method = "renderChunkLayer")
    public void preDrawEnts(RenderType $$0, PoseStack $$1, double $$2, double $$3, double $$4, Matrix4f $$5, CallbackInfo ci) {
        if ($$0 == RenderType.solid()) {
            TemplateShaderTest.draw(
                    $$1,
                    $$2,
                    $$3,
                    $$4
            );
            $$0.setupRenderState();
        }
    }
}

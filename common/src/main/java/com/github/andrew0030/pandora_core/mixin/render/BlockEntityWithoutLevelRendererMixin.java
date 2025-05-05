package com.github.andrew0030.pandora_core.mixin.render;

import com.github.andrew0030.pandora_core.client.render.BEWLRManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    // optifine calls this method "renderRaw"
    @Inject(method = {"renderByItem", "renderRaw"}, at = @At("HEAD"), cancellable = true)
    public void injectRenderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        BlockEntityWithoutLevelRenderer renderer = BEWLRManager.get(stack.getItem());
        if (renderer != null) {
            renderer.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
            ci.cancel();
        }
    }
}
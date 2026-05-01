package com.github.andrew0030.pandora_core.mixin.render;

import com.github.andrew0030.pandora_core.client.render.BEWLRManager;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    // TODO: investigate if this mixin works with OptiFine
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    public void injectAfterRenderModelList(ItemStack stack, ItemDisplayContext context, boolean applyLeftHandTransform, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BakedModel bakedModel, CallbackInfo ci) {
        PaCoRegistryObject<BlockEntityWithoutLevelRenderer> renderer = BEWLRManager.get(stack.getItem());
        if (renderer != null) {
            renderer.get().renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    // TODO: investigate if this mixin works with OptiFine (OptiFine changes name of "renderByItem" to "renderRaw")
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", shift = At.Shift.AFTER))
    public void injectAfterRenderByItem(ItemStack stack, ItemDisplayContext context, boolean applyLeftHandTransform, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BakedModel bakedModel, CallbackInfo ci) {
        PaCoRegistryObject<BlockEntityWithoutLevelRenderer> renderer = BEWLRManager.get(stack.getItem());
        if (renderer != null) {
            renderer.get().renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
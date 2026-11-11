package com.github.andrew0030.pandora_core.mixin.render.dynamic;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.render.dynamic.PaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.item.IPaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.item.IPaCoDynamicRendererTracker;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0))
    public void saveEarlyPoseStack(
            ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model, CallbackInfo ci,
            @Share("ignoreTransforms") LocalBooleanRef ignoreTransforms,
            @Share("earlyPose") LocalRef<Matrix4f> earlyPose,
            @Share("earlyNormal") LocalRef<Matrix3f> earlyNormal
    ) {
        // If the renderer ignores the item transforms, we need to cache the non-transformed pose stack
        PaCoDynamicItemRenderer renderer = ((IPaCoDynamicItemRenderer) stack.getItem()).pandoraCore$getDynamicItemRenderer();
        if (renderer == null) return;
        boolean flag = renderer.ignoreAttachedTransforms(stack, context, packedLight, packedOverlay, model);
        // Technically we could call the method again bellow, but since the checking logic may be expensive I chose to cache the value instead
        ignoreTransforms.set(flag);
        if (flag) {
            earlyPose.set(poseStack.last().pose());
            earlyNormal.set(poseStack.last().normal());
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = At.Shift.AFTER))
    public void injectAfterRenderModelList(
            ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model, CallbackInfo ci,
            @Share("ignoreTransforms") LocalBooleanRef ignoreTransforms,
            @Share("earlyPose") LocalRef<Matrix4f> earlyPose,
            @Share("earlyNormal") LocalRef<Matrix3f> earlyNormal
    ) {
        // Early exit if the item doesn't have a dynamic renderer attached
        PaCoDynamicItemRenderer renderer = ((IPaCoDynamicItemRenderer) stack.getItem()).pandoraCore$getDynamicItemRenderer();
        if (renderer == null) return;
        // Exits if this stack instance has already been rendered (needed to prevent recursive rendering)
        IPaCoDynamicRendererTracker tracker = (IPaCoDynamicRendererTracker)(Object)stack;
        if (tracker.pandoraCore$isDynamicRendering()) return;
        // If renderer wasn't null and the stack is new we simply: add tracking -> render -> remove tracking
        tracker.pandoraCore$setDynamicRendering(true);

        float partialTick = PaCoClientTicker.getPartialTick();
        poseStack.pushPose();
        try {
            if (ignoreTransforms.get()) {
                poseStack.last().pose().set(earlyPose.get());
                poseStack.last().normal().set(earlyNormal.get());
            }
            renderer.render(stack, partialTick, context, poseStack, buffer, packedLight, packedOverlay, model);
        } finally {
            poseStack.popPose();
            tracker.pandoraCore$setDynamicRendering(false);
        }

        // Post render transforms that will apply  to the item model / BEWLR
        renderer.applyPostRenderTransforms(stack, partialTick, context, poseStack);
    }
}
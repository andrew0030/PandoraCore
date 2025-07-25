package com.github.andrew0030.pandora_core.mixin.camera;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderer_CameraMixin {
    @Shadow @Final private Camera mainCamera;

    // Used to apply camera rotation on the z-axis (roll)
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void applyPaCoCameraZRot(float partialTick, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(((IPaCoSetCameraRotation) this.mainCamera).pandoraCore$getZRot()));
    }
}
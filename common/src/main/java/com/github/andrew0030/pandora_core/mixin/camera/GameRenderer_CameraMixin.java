package com.github.andrew0030.pandora_core.mixin.camera;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCameraTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRenderer_CameraMixin {
    @Shadow @Final private Camera mainCamera;

    // Used to apply camera rotation on the z-axis (roll)
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void applyPaCoCameraZRot(float partialTick, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(((IPaCoCameraTransforms) this.mainCamera).pandoraCore$getZRot()));
    }

    // Used to modify the Fov
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void applyPaCoCameraFov(Camera camera, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir) {
        double fov = cir.getReturnValue();
        // Checks if the fov is already out of bounds due to some other mod
        if (fov < 1.0 || fov > 179.0) return;
        // Applies the fov offset and keeps the value within 1-179 to avoid breaking/inverting rendering
        fov = Mth.clamp(fov + ((IPaCoCameraTransforms) camera).pandoraCore$getFovOffset(), 1, 179);
        cir.setReturnValue(fov);
    }
}
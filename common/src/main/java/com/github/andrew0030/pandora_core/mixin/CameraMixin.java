package com.github.andrew0030.pandora_core.mixin;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements IPaCoSetCameraRotation {

    @Shadow public abstract void setRotation(float yRot, float xRot);
    @Unique private float PaCoZRot;

    @Inject(method = "setup", at = @At("TAIL"))
    public void cameraShaker(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        ScreenShakeManager.updateCamera(((Camera)(Object) this), partialTick);
    }

    @Override
    public void pandoraCore$setRotation(float yaw, float pitch, float roll) {
        this.PaCoZRot = roll;
        this.setRotation(yaw, pitch);
    }

    @Override
    public float pandoraCore$getZRot() {
        return this.PaCoZRot;
    }
}
package com.github.andrew0030.pandora_core.mixin.camera;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MathUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements IPaCoSetCameraRotation {
    @Shadow private Vec3 position;

    @Shadow protected abstract void setPosition(double $$0, double $$1, double $$2);

    @Shadow protected abstract void move(double $$0, double $$1, double $$2);

    @Shadow @Final private BlockPos.MutableBlockPos blockPosition;

    @Shadow public abstract void setRotation(float $$0, float $$1);

    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;

    @Shadow protected abstract double getMaxZoom(double $$0);

    @Shadow private boolean detached;
    @Shadow private BlockGetter level;

    @Shadow protected abstract void setPosition(Vec3 $$0);

    @Shadow private Entity entity;
    @Unique private float pandoraCore$xRot;
    @Unique private float pandoraCore$yRot;
    @Unique private float pandoraCore$zRot;

    @Inject(method = "setup", at = @At("TAIL"))
    public void cameraShaker(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        ScreenShakeManager.updateCamera(((Camera)(Object) this), partialTick);
    }

    @Override
    public void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick) {
//        this.pandoraCore$xRot = xRot;
//        this.pandoraCore$yRot = yRot;
        this.pandoraCore$zRot = zRot;
        // This is a hack to make the game think the camera moved, as by default zRot doesn't trigger cull-frustum updates.
        // There is probably a more "elegant" solution by doing some mixin jank, but for the sake of simplicity this will do.
        if (zRot != 0.0F)
            xRot += partialTick * 0.000001F;
        this.setRotation(this.yRot + yRot, this.xRot + xRot);

//        this.setPosition(this.position.x() + xRot, this.position.y() + yRot, this.position.z() + zRot);
//        this.move(xRot, yRot, zRot);

        // Moves camera relative to rotation
//        this.move(
//                pandoraCore$getMaxZoom(xRot, this.forwards), // X is forwards/backwards
//                pandoraCore$getMaxZoom(yRot, this.up),       // Y is camera up/down
//                pandoraCore$getMaxZoom(zRot, this.left)      // Z is left/right
//        );

//        this.xRot += xRot;
//        this.yRot += yRot;
//        this.pandoraCore$zRot = zRot;
//        this.rotation.rotationYXZ(-yRot * Mth.DEG_TO_RAD, xRot * Mth.DEG_TO_RAD, zRot * Mth.DEG_TO_RAD);
//        this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
//        this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
//        this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
    }

    @Unique
    private double pandoraCore$getMaxZoom(double offset, Vector3f axisDirection) {
        if (offset == 0.0) return 0.0;

        Vec3 direction = new Vec3(axisDirection.x(), axisDirection.y(), axisDirection.z()).normalize();
        double finalDistance = offset;

        float spacing = 0.1F;
        for (int i = 0; i < 8; ++i) {
            float fx = (float) ((i & 1) * 2 - 1) * spacing;
            float fy = (float) (((i >> 1) & 1) * 2 - 1) * spacing;
            float fz = (float) (((i >> 2) & 1) * 2 - 1) * spacing;

            Vec3 offsetStart = this.position.add(fx, fy, fz);
            Vec3 offsetEnd = offsetStart.add(direction.scale(offset));

            ClipContext context = new ClipContext(offsetStart, offsetEnd, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity);
            HitResult hit = this.level.clip(context);

            if (hit.getType() != HitResult.Type.MISS) {
                double distance = hit.getLocation().distanceTo(offsetStart);
                if (distance < Math.abs(finalDistance)) {
                    finalDistance = Math.copySign(distance, offset);
                }
            }
        }

        return finalDistance;
    }

    @Override
    public float pandoraCore$getXRot() {
        return this.pandoraCore$xRot;
    }

    @Override
    public float pandoraCore$getYRot() {
        return pandoraCore$yRot;
    }

    @Override
    public float pandoraCore$getZRot() {
        return this.pandoraCore$zRot;
    }
}
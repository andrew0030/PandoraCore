package com.github.andrew0030.pandora_core.mixin.camera;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCameraTransforms;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements IPaCoCameraTransforms {
    @Shadow private Vec3 position;
    @Shadow protected abstract void setPosition(double $$0, double $$1, double $$2);
    @Shadow protected abstract void move(double $$0, double $$1, double $$2);
    @Shadow public abstract void setRotation(float $$0, float $$1);
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;
    @Shadow private BlockGetter level;
    @Shadow private Entity entity;

    @Unique private static final Vec3 WORLD_X = new Vec3(1, 0, 0);
    @Unique private static final Vec3 WORLD_Y = new Vec3(0, 1, 0);
    @Unique private static final Vec3 WORLD_Z = new Vec3(0, 0, 1);
    @Unique private float pandoraCore$zRot;
    @Unique private float pandoraCore$zRotOld;
    @Unique private float pandoraCore$fovOffset;
    @Unique private float pandoraCore$fovOffsetOld;

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    public void cameraShakerEarly(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        // Called before the 3rd-person offsets are applied
        ScreenShakeManager.updateCameraEarly(((Camera)(Object) this), partialTick);
    }

    @Inject(method = "setup", at = @At("TAIL"))
    public void cameraShaker(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        // Called after all vanilla offsets have been applied
        ScreenShakeManager.updateCamera(((Camera)(Object) this), partialTick); // TODO make camera position the default
    }

    @Override
    public void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick) {
        this.pandoraCore$zRot = zRot;
        // This is a hack to make the game think the camera moved, as by default zRot doesn't trigger cull-frustum updates.
        // There is probably a more "elegant" solution by doing some mixin jank, but for the sake of simplicity this will do.
        if (this.pandoraCore$zRot != this.pandoraCore$zRotOld)
            xRot += partialTick * 0.00001F;
        this.setRotation(this.yRot + yRot, this.xRot + xRot);
        this.pandoraCore$zRotOld = this.pandoraCore$zRot;
    }

    @Override
    public void pandoraCore$setPositionRelative(float horizontalOffset, float verticalOffset, float depthOffset) {
        // Moves camera relatively
        this.move(
                pandoraCore$getMaxZoom(depthOffset, this.forwards), // forwards/backwards
                pandoraCore$getMaxZoom(verticalOffset, this.up),    // up/down
                pandoraCore$getMaxZoom(horizontalOffset, this.left) // left/right
        );
    }

    @Override
    public void pandoraCore$setPositionAbsolute(float xPos, float yPos, float zPos) {
        // Moves camera absolutely
        this.setPosition(
                this.position.x() + pandoraCore$getMaxZoom(xPos, WORLD_X),
                this.position.y() + pandoraCore$getMaxZoom(yPos, WORLD_Y),
                this.position.z() + pandoraCore$getMaxZoom(zPos, WORLD_Z)
        );
    }

    @Override
    public void pandoraCore$setFovOffset(float fovOffset, float partialTick) {
        this.pandoraCore$fovOffset = fovOffset;
        // This is a hack to make the game think the camera moved, as by default fov doesn't trigger cull-frustum updates.
        // There is probably a more "elegant" solution by doing some mixin jank, but for the sake of simplicity this will do.
        if (this.pandoraCore$fovOffset != this.pandoraCore$fovOffsetOld)
            this.setRotation(this.yRot, this.xRot + (partialTick * 0.00001F));
        this.pandoraCore$fovOffsetOld = this.pandoraCore$fovOffset;
    }

    @Unique
    private double pandoraCore$getMaxZoom(double offset, Vector3f axisDirection) {
        // Since the cameras values are stored as Vector3f but later on we need Vec3, I decided to store
        // the vectors for absolute positions (x, y, z) as Vec3, as they don't require normalizing.
        // And for relative positions, this method normalizes the values and calls the other one.
        Vec3 direction = new Vec3(axisDirection.x(), axisDirection.y(), axisDirection.z()).normalize();
        return this.pandoraCore$getMaxZoom(offset, direction);
    }

    @Unique
    private double pandoraCore$getMaxZoom(double offset, Vec3 axisDirection) {
        if (offset == 0.0) return 0.0;

        double finalDistance = offset;
        float spacing = 0.1F;
        for (int i = 0; i < 8; ++i) {
            float fx = (float) ((i & 1) * 2 - 1) * spacing;
            float fy = (float) (((i >> 1) & 1) * 2 - 1) * spacing;
            float fz = (float) (((i >> 2) & 1) * 2 - 1) * spacing;

            Vec3 offsetStart = this.position.add(fx, fy, fz);
            Vec3 offsetEnd = offsetStart.add(axisDirection.x() * offset, axisDirection.y() * offset, axisDirection.z() * offset);
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
    public float pandoraCore$getZRot() {
        return this.pandoraCore$zRot;
    }

    @Override
    public float pandoraCore$getFovOffset() {
        return this.pandoraCore$fovOffset;
    }
}
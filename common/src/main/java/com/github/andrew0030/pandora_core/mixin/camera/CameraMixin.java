package com.github.andrew0030.pandora_core.mixin.camera;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
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
public abstract class CameraMixin implements IPaCoSetCameraRotation {
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

    @Unique private static final Vector3f WORLD_X = new Vector3f(1, 0, 0);
    @Unique private static final Vector3f WORLD_Y = new Vector3f(0, 1, 0);
    @Unique private static final Vector3f WORLD_Z = new Vector3f(0, 0, 1);
    @Unique private float pandoraCore$zRot;
    @Unique private float pandoraCore$zRotOld;

    @Inject(method = "setup", at = @At("TAIL"))
    public void cameraShaker(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        ScreenShakeManager.updateCamera(((Camera)(Object) this), partialTick);
    }

    @Override
    public void pandoraCore$setRotation(float xRot, float yRot, float zRot, float partialTick) {
        this.pandoraCore$zRot = zRot;
        // This is a hack to make the game think the camera moved, as by default zRot doesn't trigger cull-frustum updates.
        // There is probably a more "elegant" solution by doing some mixin jank, but for the sake of simplicity this will do.
        if (this.pandoraCore$zRot != this.pandoraCore$zRotOld)
            xRot += partialTick * 0.000001F;
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

    // TODO: Optimize this method for max performance
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
    public float pandoraCore$getZRot() {
        return this.pandoraCore$zRot;
    }
}
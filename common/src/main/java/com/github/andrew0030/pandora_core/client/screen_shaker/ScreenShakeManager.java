package com.github.andrew0030.pandora_core.client.screen_shaker;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Helper class that allows playing ScreenShakes. */
public class ScreenShakeManager {
    protected static final List<ScreenShake> ACTIVE_SHAKES = new ArrayList<>();
    protected static float yawOffset;
    protected static float pitchOffset;
    protected static float rollOffset;

    /** Updates the {@link Camera}, used to apply the total offset from all {@link ScreenShakeManager#ACTIVE_SHAKES}. */
    @ApiStatus.Internal
    public static void updateCamera(Camera camera, float partialTick) {
        yawOffset = 0.0F;
        pitchOffset = 0.0F;
        rollOffset = 0.0F;

        //TODO probably clamp shaking to a max value and have a global multiplier
        for (ScreenShake shake : ACTIVE_SHAKES) {
            yawOffset += shake.getYawOffset(partialTick);
            pitchOffset += shake.getPitchOffset(partialTick);
            rollOffset += shake.getRollOffset(partialTick);
        }

        // TODO: probably add a getZRot so value can be modified and screen shake applied on top
        ((IPaCoSetCameraRotation) camera).pandoraCore$setRotation(camera.getYRot() + yawOffset, camera.getXRot() + pitchOffset, rollOffset);
    }

    /**
     * Updates all the active {@link ScreenShake} instances, and removes
     * them from {@link ScreenShakeManager#ACTIVE_SHAKES} once they are finished.
     */
    @ApiStatus.Internal
    public static void tickCameraShakes() {
        Iterator<ScreenShake> iterator = ACTIVE_SHAKES.iterator();
        while (iterator.hasNext()) {
            ScreenShake shake = iterator.next();
            shake.tick();
            if (shake.isFinished()) {
                iterator.remove();
            }
        }
    }

    /**
     * Used to add a new active {@link ScreenShake}.
     * @param shake The {@link ScreenShake} that will be played
     */
    public static void addScreenShake(ScreenShake shake) {
        ACTIVE_SHAKES.add(shake);
    }
}
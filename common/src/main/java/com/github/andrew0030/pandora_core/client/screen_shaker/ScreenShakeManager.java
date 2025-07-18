package com.github.andrew0030.pandora_core.client.screen_shaker;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoSetCameraRotation;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: Maybe rename the class to something non screen shake related, seeing how now this class handles more than just screen shaking
/** Helper class that allows playing ScreenShakes. */
public class ScreenShakeManager {
    protected static final List<ScreenShake> CONSTRAINED_SHAKES = new ArrayList<>();
    protected static final List<ScreenShake> UNCONSTRAINED_SHAKES = new ArrayList<>();
    protected static float pitchOffset, yawOffset, rollOffset;
    protected static float xOffsetRelative, yOffsetRelative, zOffsetRelative;
    protected static float xOffsetAbsolute, yOffsetAbsolute, zOffsetAbsolute;

    /**
     * Updates the {@link Camera}, used to apply the total offset from all
     * {@link ScreenShakeManager#CONSTRAINED_SHAKES} and {@link ScreenShakeManager#UNCONSTRAINED_SHAKES}.
     */
    @ApiStatus.Internal
    public static void updateCamera(Camera camera, float partialTick) {
        pitchOffset = 0.0F;
        yawOffset   = 0.0F;
        rollOffset  = 0.0F;
        xOffsetRelative = 0.0F;
        yOffsetRelative = 0.0F;
        zOffsetRelative = 0.0F;
        xOffsetAbsolute = 0.0F;
        yOffsetAbsolute = 0.0F;
        zOffsetAbsolute = 0.0F;

        //TODO: Maybe skip all constrained shake offset logic if "multiplier" or "limit" are 0 ?

        // Accumulates constrained shakes
        for (ScreenShake shake : CONSTRAINED_SHAKES) {
            pitchOffset += shake.getPitchOffset(partialTick); // x-axis
            yawOffset   += shake.getYawOffset(partialTick);   // y-axis
            rollOffset  += shake.getRollOffset(partialTick);  // z-axis
        }

        // Applies global shake multiplier
        float multiplier = 1.0F; // TODO: make this a config option
        pitchOffset *= multiplier;
        yawOffset   *= multiplier;
        rollOffset  *= multiplier;

        // Soft limits each axis
        float limit = 90F; // TODO: make this a config option, and prevent it from being called if the value is 0 because divided by zero...
        pitchOffset = ScreenShakeManager.softLimit(pitchOffset, limit);
        yawOffset   = ScreenShakeManager.softLimit(yawOffset, limit);
        rollOffset  = ScreenShakeManager.softLimit(rollOffset, limit);

        // Adds unconstrained shakes (raw)
        for (ScreenShake shake : UNCONSTRAINED_SHAKES) {
            pitchOffset += shake.getPitchOffset(partialTick); // x-axis
            yawOffset   += shake.getYawOffset(partialTick);   // y-axis
            rollOffset  += shake.getRollOffset(partialTick);  // z-axis
        }

        // Sets the camera's rotation offsets
        ((IPaCoSetCameraRotation) camera).pandoraCore$setRotation(pitchOffset, yawOffset, rollOffset, partialTick);
        // Sets the camera's position offsets relative to rotation
        ((IPaCoSetCameraRotation) camera).pandoraCore$setPositionRelative(xOffsetRelative, yOffsetRelative, zOffsetRelative);
        // Sets the camera's position offsets
        ((IPaCoSetCameraRotation) camera).pandoraCore$setPositionAbsolute(xOffsetAbsolute, yOffsetAbsolute, zOffsetAbsolute);
    }

    /**
     * Applies a soft limiting function to the given value using a scaled hyperbolic tangent (tanh).
     * <p>
     * Unlike a hard clamp which abruptly stops values at a fixed threshold, this method smoothly
     * compresses large values toward the specified {@code limit}, preserving motion and oscillation.
     *
     * @param value the input value to be soft-limited
     * @param limit the maximum magnitude to which the output will asymptotically approach
     * @return a smoothly limited value that never exceeds {@code limit}, but transitions softly near it
     */
    private static float softLimit(float value, float limit) {
        return (float) (Math.tanh(value / limit) * limit);
    }

    /**
     * Updates all the active {@link ScreenShake} instances, and removes
     * them from their corresponding shake list, once they are finished.
     */
    @ApiStatus.Internal
    public static void tickCameraShakes() {
        // Ticks and cleans constrained screen shakes
        Iterator<ScreenShake> iterator = CONSTRAINED_SHAKES.iterator();
        while (iterator.hasNext()) {
            ScreenShake shake = iterator.next();
            shake.tick();
            if (shake.isFinished()) {
                iterator.remove();
            }
        }
        // Ticks and cleans unconstrained screen shakes
        iterator = UNCONSTRAINED_SHAKES.iterator();
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
        if (shake.hasGeneralConstrains()) {
            CONSTRAINED_SHAKES.add(shake);
        } else {
            UNCONSTRAINED_SHAKES.add(shake);
        }
    }
}
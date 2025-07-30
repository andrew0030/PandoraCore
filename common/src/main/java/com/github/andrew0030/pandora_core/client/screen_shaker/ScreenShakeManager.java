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
    protected static float horizontalOffset, verticalOffset, depthOffset;
    protected static float xOffset, yOffset, zOffset;
    protected static float fovOffset;

    /**
     * Updates the {@link Camera}, used to apply the total offset from all
     * {@link ScreenShakeManager#CONSTRAINED_SHAKES} and {@link ScreenShakeManager#UNCONSTRAINED_SHAKES}.
     */
    @ApiStatus.Internal
    public static void updateCamera(Camera camera, float partialTick) {
        ScreenShakeManager.resetOffsets();

        //TODO: Maybe skip all constrained shake offset logic if "multiplier" or "limits" are 0 ?

        // Accumulates constrained shakes
        for (ScreenShake shake : CONSTRAINED_SHAKES) {
            ScreenShakeManager.applyAllOffsets(shake, partialTick);
        }

        // Applies global shake multiplier
        float multiplier = 1.0F; // TODO: make this a config option
        pitchOffset *= multiplier;
        yawOffset   *= multiplier;
        rollOffset  *= multiplier;
        // TODO add a multiplier for position offsets (probably should be a different value, as the rotation multiplier affects it less)

        // Soft limits each axis
        float limit = 90F; // TODO: make this a config option, and prevent it from being called if the value is 0 because divided by zero...
        pitchOffset = ScreenShakeManager.softLimit(pitchOffset, limit);
        yawOffset   = ScreenShakeManager.softLimit(yawOffset, limit);
        rollOffset  = ScreenShakeManager.softLimit(rollOffset, limit);
        // TODO add soft limits for relative and absolute position

        // Adds unconstrained shakes (raw)
        for (ScreenShake shake : UNCONSTRAINED_SHAKES) {
            ScreenShakeManager.applyAllOffsets(shake, partialTick);
        }

        // Sets the camera's rotation offsets
        ((IPaCoSetCameraRotation) camera).pandoraCore$setRotation(pitchOffset, yawOffset, rollOffset, partialTick);
        // Sets the camera's position offsets relative to rotation
        ((IPaCoSetCameraRotation) camera).pandoraCore$setPositionRelative(horizontalOffset, verticalOffset, depthOffset);
        // Sets the camera's position offsets
        ((IPaCoSetCameraRotation) camera).pandoraCore$setPositionAbsolute(xOffset, yOffset, zOffset);
        // Sets the camera's FOV offset
        ((IPaCoSetCameraRotation) camera).pandoraCore$setFOVOffset(fovOffset, partialTick);
    }

    /** Sets all offsets to {@code 0.0F}. */
    private static void resetOffsets() {
        pitchOffset = yawOffset = rollOffset = 0.0F;
        horizontalOffset = verticalOffset = depthOffset = 0.0F;
        xOffset = yOffset = zOffset = 0.0F;
        fovOffset = 0.0F;
    }

    /**
     * Retrieves all offsets of the given {@link ScreenShake} and applies them to the {@link ScreenShakeManager}.
     * @param shake       The {@link ScreenShake} instance that holds the offsets
     * @param partialTick The current partial tick
     */
    private static void applyAllOffsets(ScreenShake shake, float partialTick) {
        // Rotation
        pitchOffset += shake.getPitchOffset(partialTick); // x-axis
        yawOffset   += shake.getYawOffset(partialTick);   // y-axis
        rollOffset  += shake.getRollOffset(partialTick);  // z-axis
        // Relative Position
        horizontalOffset += shake.getHorizontalOffset(partialTick);
        verticalOffset   += shake.getVerticalOffset(partialTick);
        depthOffset      += shake.getDepthOffset(partialTick);
        // Absolute Position
        xOffset += shake.getXOffset(partialTick);
        yOffset += shake.getYOffset(partialTick);
        zOffset += shake.getZOffset(partialTick);
        // FOV
        fovOffset += shake.getFOVOffset(partialTick);
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
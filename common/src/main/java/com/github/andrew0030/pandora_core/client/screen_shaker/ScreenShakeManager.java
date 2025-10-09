package com.github.andrew0030.pandora_core.client.screen_shaker;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoCameraTransforms;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

// TODO: Maybe rename the class to something non screen shake related, seeing how now this class handles more than just screen shaking
/** Helper class that allows playing ScreenShakes. */
public class ScreenShakeManager {
    protected static final EnumMap<ShakeType, List<ScreenShake>> SCREEN_SHAKES = new EnumMap<>(ShakeType.class);
    protected static float pitchOffset, yawOffset, rollOffset;
    protected static float horizontalOffset, verticalOffset, depthOffset;
    protected static float xOffset, yOffset, zOffset;
    protected static float fovOffset;

    static {
        // Populates the map so we can safely get each list without causing null-pointers
        for (ShakeType type : ShakeType.values())
            SCREEN_SHAKES.put(type, new ArrayList<>());
    }

    /**
     * Updates the {@link Camera}, used to apply the total offset from all
     * {@code EARLY_CONSTRAINED} shakes and {@code  EARLY_UNCONSTRAINED} shakes.
     *
     * @apiNote This method gets called before the 3rd person camera offsets are applied, meaning that collision
     * checks with the world originate from the {@code player head}, rather than the {@code camera position}.
     */
    @ApiStatus.Internal
    public static void updateCameraEarly(Camera camera, float partialTick) {
        ScreenShakeManager.applyCameraTransforms(
                camera, partialTick,
                SCREEN_SHAKES.get(ShakeType.EARLY_CONSTRAINED),
                SCREEN_SHAKES.get(ShakeType.EARLY_UNCONSTRAINED)
        );
    }

    /**
     * Updates the {@link Camera}, used to apply the total offset from all
     * {@code  CONSTRAINED} shakes and {@code  UNCONSTRAINED} shakes.
     *
     * @apiNote This method gets called after the 3rd person camera offsets are applied, meaning
     * that collision checks with the world originate from the {@code camera position}.
     */
    @ApiStatus.Internal
    public static void updateCamera(Camera camera, float partialTick) {
        ScreenShakeManager.applyCameraTransforms(
                camera, partialTick,
                SCREEN_SHAKES.get(ShakeType.CONSTRAINED),
                SCREEN_SHAKES.get(ShakeType.UNCONSTRAINED)
        );
    }

    private static void applyCameraTransforms(Camera camera, float partialTick, List<ScreenShake> constrained, List<ScreenShake> unconstrained) {
        // Resets previous camera offsets
        ScreenShakeManager.resetOffsets();
        //TODO: Maybe skip all constrained shake offset logic if "multiplier" or "limits" are 0 ?
        float multiplier = 1.0F; // TODO: make this a config option
        // TODO: make these config options, and prevent them from being called if their values are 0 because divided by zero...
        float rotLimit = 45F;
        float posLimit = 1.5F;
        float fovLimit = 20F;

        // Accumulates constrained shakes
        for (ScreenShake shake : constrained) {
            ScreenShakeManager.applyAllOffsets(shake, partialTick);
        }
        // Applies global shake multiplier
        ScreenShakeManager.applyMultiplier(multiplier);
        // Soft limits each axis
        ScreenShakeManager.applySoftLimits(rotLimit, posLimit, fovLimit);
        // Adds unconstrained shakes (raw)
        for (ScreenShake shake : unconstrained) {
            ScreenShakeManager.applyAllOffsets(shake, partialTick);
        }

        // Sets the camera's rotation offsets
        ((IPaCoCameraTransforms) camera).pandoraCore$setRotation(pitchOffset, yawOffset, rollOffset, partialTick);
        // Sets the camera's position offsets relative to rotation
        ((IPaCoCameraTransforms) camera).pandoraCore$setPositionRelative(horizontalOffset, verticalOffset, depthOffset);
        // Sets the camera's position offsets
        ((IPaCoCameraTransforms) camera).pandoraCore$setPositionAbsolute(xOffset, yOffset, zOffset);
        // Sets the camera's fov offset
        ((IPaCoCameraTransforms) camera).pandoraCore$setFovOffset(fovOffset, partialTick);
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
        // Fov
        fovOffset += shake.getFovOffset(partialTick);
    }

    /**
     * Applies a multiplier to all {@code EARLY_CONSTRAINED} and {@code CONSTRAINED} shakes.
     * @param multiplier  The multiplier that should be applied
     */
    private static void applyMultiplier(float multiplier) {
        // Rotation
        pitchOffset *= multiplier;
        yawOffset   *= multiplier;
        rollOffset  *= multiplier;
        // Relative Position
        horizontalOffset *= multiplier;
        verticalOffset   *= multiplier;
        depthOffset      *= multiplier;
        // Absolute Position
        xOffset *= multiplier;
        yOffset *= multiplier;
        zOffset *= multiplier;
        // Fov
        fovOffset *= multiplier;
    }

    /**
     * Applies the soft-limits (constrains) to all {@code EARLY_CONSTRAINED} and {@code CONSTRAINED} shakes.
     * @param rotLimit The max rotation in degrees all constrained shakes can reach
     * @param posLimit The max distance in blocks all constrained shakes can reach
     * @param fovLimit The max angle change that can be applied to the fov by all constrained shakes
     */
    private static void applySoftLimits(float rotLimit, float posLimit, float fovLimit) {
        // Rotation
        pitchOffset = ScreenShakeManager.softLimit(pitchOffset, rotLimit);
        yawOffset   = ScreenShakeManager.softLimit(yawOffset, rotLimit);
        rollOffset  = ScreenShakeManager.softLimit(rollOffset, rotLimit);
        // Relative Position
        horizontalOffset = ScreenShakeManager.softLimit(horizontalOffset, posLimit);
        verticalOffset   = ScreenShakeManager.softLimit(verticalOffset, posLimit);
        depthOffset      = ScreenShakeManager.softLimit(depthOffset, posLimit);
        // Absolute Position
        xOffset = ScreenShakeManager.softLimit(xOffset, posLimit);
        yOffset = ScreenShakeManager.softLimit(yOffset, posLimit);
        zOffset = ScreenShakeManager.softLimit(zOffset, posLimit);
        // Fov
        fovOffset = ScreenShakeManager.softLimit(fovOffset, fovLimit);
    }

    /**
     * Applies a soft limiting function to the given value using a scaled hyperbolic tangent (tanh).
     * <p>
     * Unlike a hard clamp which abruptly stops values at a fixed threshold, this method smoothly
     * compresses large values toward the specified {@code limit}, preserving motion and oscillation.
     * </p>
     * Compression also only starts after a certain percentage, so small values remain unchanged.
     *
     * @param value the input value to be soft-limited
     * @param limit the maximum magnitude to which the output will asymptotically approach
     * @return a smoothly limited value that never exceeds {@code limit}, but tries to transition softly near it
     */
    private static float softLimit(float value, float limit) {
        float absValue = Math.abs(value);
        float softStart = 0.8f; // Starts soft easing after (X)% of the limit
        float curveSharpness = 0.8F; // The sharpness of the curve past "soft start"

        // Values under the "soft start" percentage are returned as is
        if (absValue <= softStart * limit)
            return value;

        // Values over "soft start" percentage are compressed
        float easedRange = limit - softStart * limit;
        float easedInput = (absValue - softStart * limit) / easedRange;
        float easedOutput = (float) (Math.tanh(easedInput * curveSharpness) * easedRange);
        return Math.signum(value) * (softStart * limit + easedOutput);
    }

    /**
     * Updates all the active {@link ScreenShake} instances, and removes
     * them from their corresponding shake list, once they are finished.
     */
    @ApiStatus.Internal
    public static void tickCameraShakes() {
        // Ticks and cleans early constrained screen shakes
        ScreenShakeManager.processShakes(ShakeType.EARLY_CONSTRAINED);
        // Ticks and cleans constrained screen shakes
        ScreenShakeManager.processShakes(ShakeType.CONSTRAINED);
        // Ticks and cleans early unconstrained screen shakes
        ScreenShakeManager.processShakes(ShakeType.EARLY_UNCONSTRAINED);
        // Ticks and cleans unconstrained screen shakes
        ScreenShakeManager.processShakes(ShakeType.UNCONSTRAINED);
    }

    private static void processShakes(ShakeType type) {
        Iterator<ScreenShake> iterator = SCREEN_SHAKES.get(type).iterator();
        while (iterator.hasNext()) {
            ScreenShake shake = iterator.next();
            shake.tick();
            if (shake.isFinished())
                iterator.remove();
        }
    }

    /**
     * Used to add a new active {@link ScreenShake}.
     * @param shake The {@link ScreenShake} that will be played
     */
    public static void addScreenShake(ScreenShake shake) {
        ShakeType type;
        if (shake.hasGeneralConstrains()) {
            type = shake.usesPlayerAsOrigin() ? ShakeType.EARLY_CONSTRAINED : ShakeType.CONSTRAINED;
        } else {
            type = shake.usesPlayerAsOrigin() ? ShakeType.EARLY_UNCONSTRAINED : ShakeType.UNCONSTRAINED;
        }
        SCREEN_SHAKES.get(type).add(shake);
    }

    /**
     * The various screen shake types:
     * <ul>
     *     <li>{@code EARLY_CONSTRAINED:} Called before 3rd-person offsets are applied and uses constrains system.</li>
     *     <li>{@code CONSTRAINED:} Called after 3rd-person offsets are applied and uses constrains system.</li>
     *     <li>{@code EARLY_UNCONSTRAINED:} Called before 3rd-person offsets are applied and doesn't use constrains system.</li>
     *     <li>{@code UNCONSTRAINED:} Called after 3rd-person offsets are applied and doesn't use constrains system.</li>
     * </ul>
     */
    protected enum ShakeType {
        EARLY_CONSTRAINED,
        CONSTRAINED,
        EARLY_UNCONSTRAINED,
        UNCONSTRAINED
    }
}
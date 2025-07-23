package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.curve_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.EasingDirection;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.PitchDirection;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.RollDirection;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.YawDirection;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** A curve based {@link ScreenShake}, that supports per axis {@link EasingDirection}, {@link Easing}. */
public class CurveScreenShake extends ScreenShake {
    protected static final RandomSource random = RandomSource.create();
    protected float yawDegrees = 0.0F;
    protected float yawBounces = 0.0F;
    protected EasingDirection yawEasingDirection = EasingDirection.NONE;
    protected Easing yawEasingType = Easing.LINEAR;
    protected float pitchDegrees = 0.0F;
    protected float pitchBounces = 0.0F;
    protected EasingDirection pitchEasingDirection = EasingDirection.NONE;
    protected Easing pitchEasingType = Easing.LINEAR;
    protected float rollDegrees = 0.0F;
    protected float rollBounces = 0.0F;
    protected EasingDirection rollEasingDirection = EasingDirection.NONE;
    protected Easing rollEasingType = Easing.LINEAR;

    /**
     * A new {@link CurveScreenShake} instance, methods can/should be
     * chained onto this to specify the exact behavior it should have.
     * @param duration How long this {@link CurveScreenShake} should last, measured in ticks.
     */
    public CurveScreenShake(int duration) {
        super(duration);
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's yaw (left/right movement).
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     *                  <strong>Note</strong>: While "bounces" is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setYaw(YawDirection direction, float degrees, float bounces) {
        this.yawDegrees = Math.abs(degrees) * direction.getValue(random);
        this.yawBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's pitch (up/down movement).
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     *                  <strong>Note</strong>: While "bounces" is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setPitch(PitchDirection direction, float degrees, float bounces) {
        this.pitchDegrees = Math.abs(degrees) * direction.getValue(random);
        this.pitchBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's roll (rotational movement around the forward axis).
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     *                  <strong>Note</strong>: While "bounces" is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setRoll(RollDirection direction, float degrees, float bounces) {
        this.rollDegrees = Math.abs(degrees) * direction.getValue(random);
        this.rollBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies the easing direction of the yaw curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setYawEasing(EasingDirection easingDirection, Easing easingType) {
        this.yawEasingDirection = easingDirection;
        this.yawEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the pitch curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setPitchEasing(EasingDirection easingDirection, Easing easingType) {
        this.pitchEasingDirection = easingDirection;
        this.pitchEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the roll curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setRollEasing(EasingDirection easingDirection, Easing easingType) {
        this.rollEasingDirection = easingDirection;
        this.rollEasingType = easingType;
        return this;
    }

    @Override
    public float getYawOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.yawDegrees, this.yawBounces, this.yawEasingDirection, this.yawEasingType);
    }

    @Override
    public float getPitchOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.pitchDegrees, this.pitchBounces, this.pitchEasingDirection, this.pitchEasingType);
    }

    @Override
    public float getRollOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.rollDegrees, this.rollBounces, this.rollEasingDirection, this.rollEasingType);
    }

    // TODO implement position offset logic
    @Override
    public float getHorizontalOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getVerticalOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getDepthOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getXOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getYOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getZOffset(float partialTick) {
        return 0;
    }

    /** @return The offset based on the given values */
    protected float calculateOffset(float partialTick, float degrees, float bounces, EasingDirection easingDirection, Easing easingType) {
        if (degrees == 0 || bounces == 0) return 0;
        // We add partial ticks to the tick counter, so the value we work with is more precise
        float totalTime = this.tickCount + partialTick;
        // We get the time converted to a 0.0F -> 1.0F value, and we apply easing to it.
        float normalizedTime = easingType.apply(totalTime / this.duration);
        // Based on the given easing direction and the normalized time, we create a multiplier between 0.0F - 1.0F
        float easingDirectionMultiplier = switch (easingDirection) {
            case NONE -> 1.0F;
            case IN -> normalizedTime;
            case OUT -> 1F - normalizedTime;
            case IN_OUT -> (normalizedTime <= 0.5F) ? normalizedTime * 2 : 2 * (1 - normalizedTime);
        };
        // We calculate the phase of the sine wave.
        float phase = normalizedTime * Mth.PI * bounces;

        return degrees * easingDirectionMultiplier * Mth.sin(phase);
    }
}
package com.github.andrew0030.pandora_core.client.screen_shaker.curve_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShake;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class CurveScreenShake extends ScreenShake {
    private static final RandomSource random = RandomSource.create();
    private float yawDegrees = 0.0F;
    private float yawBounces = 0.0F;
    private EasingDirection yawEasingDirection = EasingDirection.NONE;
    private Easing yawEasingType = Easing.LINEAR;
    private float pitchDegrees = 0.0F;
    private float pitchBounces = 0.0F;
    private EasingDirection pitchEasingDirection = EasingDirection.NONE;
    private Easing pitchEasingType = Easing.LINEAR;
    private float rollDegrees = 0.0F;
    private float rollBounces = 0.0F;
    private EasingDirection rollEasingDirection = EasingDirection.NONE;
    private Easing rollEasingType = Easing.LINEAR;

    public CurveScreenShake(int duration) {
        super(duration);
    }

    public CurveScreenShake setYaw(YawDirection direction, float degrees, float bounces) {
        this.yawDegrees = Math.abs(degrees) * direction.getValue(random);
        this.yawBounces = Math.max(0, bounces);
        return this;
    }

    public CurveScreenShake setPitch(PitchDirection direction, float degrees, float bounces) {
        this.pitchDegrees = Math.abs(degrees) * direction.getValue(random);
        this.pitchBounces = Math.max(0, bounces);
        return this;
    }

    public CurveScreenShake setRoll(RollDirection direction, float degrees, float bounces) {
        this.rollDegrees = Math.abs(degrees) * direction.getValue(random);
        this.rollBounces = Math.max(0, bounces);
        return this;
    }

    public CurveScreenShake setYawEasing(EasingDirection easingDirection, Easing easingType) {
        this.yawEasingDirection = easingDirection;
        this.yawEasingType = easingType;
        return this;
    }

    public CurveScreenShake setPitchEasing(EasingDirection easingDirection, Easing easingType) {
        this.pitchEasingDirection = easingDirection;
        this.pitchEasingType = easingType;
        return this;
    }

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

    private float calculateOffset(float partialTick, float degrees, float bounces, EasingDirection easingDirection, Easing easingType) {
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
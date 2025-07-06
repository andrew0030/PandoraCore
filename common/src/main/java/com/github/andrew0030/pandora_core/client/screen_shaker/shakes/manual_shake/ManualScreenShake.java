package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.manual_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;

/**
 * A manual {@link ScreenShake}, that allows for full control over the camera offsets,
 * by continuously updating offsets using the {@code setters}.
 * @implNote {@link ManualScreenShake} is not affected by the PaCo config settings,
 * so constrains need to be implemented separately.
 */
public class ManualScreenShake extends ScreenShake {
    protected float yawDegrees = 0.0F;
    protected float pitchDegrees = 0.0F;
    protected float rollDegrees = 0.0F;
    protected boolean isFinished;

    /** A new {@link ManualScreenShake} instance: */
    public ManualScreenShake() {
        super(Integer.MAX_VALUE);
    }

    /**
     * Used to flag the {@link ManualScreenShake} instance as finished,
     * and thus should be removed from the active screen shakes.
     * @param isFinished Whether the screen shake is finished and no longer needed.
     */
    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     * Specifies an offset, that gets applied to the camera's yaw (left/right movement).
     *
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setYaw(float degrees) {
        this.yawDegrees = degrees;
        return this;
    }


    /**
     * Specifies an offset, that gets applied to the camera's pitch (up/down movement).
     *
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setPitch(float degrees) {
        this.pitchDegrees = degrees;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's roll (rotational movement around the forward axis).
     *
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setRoll(float degrees) {
        this.rollDegrees = degrees;
        return this;
    }

    @Override
    public float getYawOffset(float partialTick) {
        return this.yawDegrees;
    }

    @Override
    public float getPitchOffset(float partialTick) {
        return this.pitchDegrees;
    }

    @Override
    public float getRollOffset(float partialTick) {
        return this.rollDegrees;
    }

    @Override
    public boolean hasGeneralConstrains() {
        return false;
    }
}
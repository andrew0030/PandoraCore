package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.manual_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;

/**
 * A manual {@link ScreenShake}, that allows for full control over the camera offsets,
 * by continuously updating offsets using the {@code setters}.
 * @implNote {@link ManualScreenShake} is not affected by the PaCo config settings,
 * so constrains need to be implemented separately.
 */
public class ManualScreenShake extends ScreenShake {
    protected float yawDegrees, pitchDegrees, rollDegrees = 0.0F;
    protected float horizontalOffset, verticalOffset, depthOffset = 0.0F;
    protected float xOffset, yOffset, zOffset = 0.0F;
    protected float fovOffset = 0.0F;
    protected boolean isFinished;

    /**
     * A new {@link ManualScreenShake} instance, methods can/should be
     * chained onto this to specify the exact behavior it should have.
     */
    public ManualScreenShake() {
        super(Integer.MAX_VALUE);
    }

    /**
     * A new {@link ManualScreenShake} instance, methods can/should be
     * chained onto this to specify the exact behavior it should have.
     * @param playerAsOrigin Whether to use the player as the origin for camera collision checks
     */
    public ManualScreenShake(boolean playerAsOrigin) {
        super(Integer.MAX_VALUE, playerAsOrigin);
    }

    /**
     * Used to flag the {@link ManualScreenShake} instance as finished,
     * and that it should be removed from the active screen shakes.
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
     * Specifies an offset, that gets applied to the camera's yaw (left/right rotation).
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setYaw(float degrees) {
        this.yawDegrees = degrees;
        return this;
    }


    /**
     * Specifies an offset, that gets applied to the camera's pitch (up/down rotation).
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setPitch(float degrees) {
        this.pitchDegrees = degrees;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's roll (rotation around the forward axis).
     * @param degrees The number of degrees the camera should move in the specified direction.
     */
    public ManualScreenShake setRoll(float degrees) {
        this.rollDegrees = degrees;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's horizontal position (left/right relative to rotation).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setHorizontalOffset(float distance) {
        this.horizontalOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's vertical position (up/down relative to rotation).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setVerticalOffset(float distance) {
        this.verticalOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's depth position (position along the forward axis relative to rotation).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setDepthOffset(float distance) {
        this.depthOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's x position (x position in the world).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setXOffset(float distance) {
        this.xOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's y position (y position in the world).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setYOffset(float distance) {
        this.yOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's z position (z position in the world).
     * @param distance The number of blocks the camera should move in the specified direction.
     */
    public ManualScreenShake setZOffset(float distance) {
        this.zOffset = distance;
        return this;
    }

    /**
     * Specifies an offset, that gets applied to the camera's fov.
     * @param degrees The number of degrees the camera's FOV should be changed by.
     */
    public ManualScreenShake setFovOffset(float degrees) {
        this.fovOffset = degrees;
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
    public float getHorizontalOffset(float partialTick) {
        return this.horizontalOffset;
    }

    @Override
    public float getVerticalOffset(float partialTick) {
        return this.verticalOffset;
    }

    @Override
    public float getDepthOffset(float partialTick) {
        return this.depthOffset;
    }

    @Override
    public float getXOffset(float partialTick) {
        return this.xOffset;
    }

    @Override
    public float getYOffset(float partialTick) {
        return this.yOffset;
    }

    @Override
    public float getZOffset(float partialTick) {
        return this.zOffset;
    }

    @Override
    public float getFovOffset(float partialTick) {
        return this.fovOffset;
    }

    @Override
    public boolean hasGeneralConstrains() {
        return false;
    }
}
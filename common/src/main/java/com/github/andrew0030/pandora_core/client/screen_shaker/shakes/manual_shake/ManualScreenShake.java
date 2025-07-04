package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.manual_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;

public class ManualScreenShake extends ScreenShake {
    protected float yawDegrees = 0.0F;
    protected float pitchDegrees = 0.0F;
    protected float rollDegrees = 0.0F;
    protected boolean isFinished;

    public ManualScreenShake() {
        super(Integer.MAX_VALUE);
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    public ManualScreenShake setYaw(float degrees) {
        this.yawDegrees = degrees;
        return this;
    }

    public ManualScreenShake setPitch(float degrees) {
        this.pitchDegrees = degrees;
        return this;
    }

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
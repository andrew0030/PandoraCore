package com.github.andrew0030.pandora_core.client.screen_shaker;

public abstract class ScreenShake {
    protected final int duration;
    protected int tickCount;

    public ScreenShake(int duration) {
        this.duration = Math.max(duration, 0);
//        this.intensity = intensity;
    }

    public void update() {
        this.tickCount++;
    }

    public boolean isFinished() {
        return this.tickCount > this.duration;
    }

    public abstract float getYawOffset(float partialTick);

    public abstract float getPitchOffset(float partialTick);

    public abstract float getRollOffset(float partialTick);
}
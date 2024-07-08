package com.github.andrew0030.pandora_core.client.screen_shaker.shakes;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.curve_shake.CurveScreenShake;
import net.minecraft.client.Camera;

/** Base ScreenShake, this can be extended to create new ScreenShake types, such as {@link CurveScreenShake}. */
public abstract class ScreenShake {
    protected final int duration;
    protected int tickCount;

    /** @param duration How long the {@link ScreenShake} should remain active */
    public ScreenShake(int duration) {
        this.duration = Math.max(duration, 0);
    }

    /** Gets called every tick, great for updating values over time. */
    public void tick() {
        this.tickCount++;
    }

    /** @return Whether the {@link ScreenShake} is done, and should be removed from {@link ScreenShakeManager}. */
    public boolean isFinished() {
        return this.tickCount > this.duration;
    }

    /**
     * Gets called every render tick to modify the {@link Camera} yaw offset.
     * @return The yaw (left/right) offset this {@link ScreenShake} should add to the {@link Camera}.
     */
    public abstract float getYawOffset(float partialTick);

    /**
     * Gets called every render tick to modify the {@link Camera} pitch offset.
     * @return The pitch (up/down) offset this {@link ScreenShake} should add to the {@link Camera}.
     */
    public abstract float getPitchOffset(float partialTick);

    /**
     * Gets called every render tick to modify the {@link Camera} roll offset.
     * @return The roll offset (barrel roll left/right) this {@link ScreenShake} should add to the {@link Camera}.
     */
    public abstract float getRollOffset(float partialTick);
}
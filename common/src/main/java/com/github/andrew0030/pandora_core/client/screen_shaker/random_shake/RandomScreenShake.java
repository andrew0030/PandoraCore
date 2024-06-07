package com.github.andrew0030.pandora_core.client.screen_shaker.random_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShake;

public class RandomScreenShake extends ScreenShake {

    private final int steps;

    public RandomScreenShake(int duration, int steps) {
        super(duration);
        this.steps = steps;
    }

    @Override
    public float getYawOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getPitchOffset(float partialTick) {
        return 0;
    }

    @Override
    public float getRollOffset(float partialTick) {
        return 0;
    }
}
package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums;

import net.minecraft.util.RandomSource;

public enum DepthDirection {
    FORWARDS,
    BACKWARDS,
    RANDOM;

    public int getValue(RandomSource random) {
        if (this == RANDOM) return random.nextBoolean() ? 1 : -1;
        return this == FORWARDS ? 1 : -1;
    }
}
package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums;

import net.minecraft.util.RandomSource;

public enum YawDirection {
    RIGHT,
    LEFT,
    RANDOM;

    public int getValue(RandomSource random) {
        if (this == RANDOM) return random.nextBoolean() ? 1 : -1;
        return this == RIGHT ? 1 : -1;
    }
}
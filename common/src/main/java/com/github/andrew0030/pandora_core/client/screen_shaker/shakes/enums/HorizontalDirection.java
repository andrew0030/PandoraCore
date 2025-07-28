package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums;

import net.minecraft.util.RandomSource;

public enum HorizontalDirection {
    RIGHT,
    LEFT,
    RANDOM;

    public int getValue(RandomSource random) {
        if (this == RANDOM) return random.nextBoolean() ? 1 : -1;
        return this == LEFT ? 1 : -1;
    }
}

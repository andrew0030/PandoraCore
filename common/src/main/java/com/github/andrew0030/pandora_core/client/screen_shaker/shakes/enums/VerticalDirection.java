package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums;

import net.minecraft.util.RandomSource;

public enum VerticalDirection {
    UP,
    DOWN,
    RANDOM;

    public int getValue(RandomSource random) {
        if (this == RANDOM) return random.nextBoolean() ? 1 : -1;
        return this == UP ? 1 : -1;
    }
}
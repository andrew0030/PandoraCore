package com.github.andrew0030.pandora_core.utils.easing;

@FunctionalInterface
interface EasingFunction {
    float apply(float value);
}
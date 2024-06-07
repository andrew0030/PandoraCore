package com.github.andrew0030.pandora_core.utils.easing;

@FunctionalInterface
interface EasingFunctionWithFloat {
    float apply(float value, float floatData);
}
package com.github.andrew0030.pandora_core.utils.easing;

@FunctionalInterface
interface EasingFunctionWithInt {
    float apply(float value, int intData);
}
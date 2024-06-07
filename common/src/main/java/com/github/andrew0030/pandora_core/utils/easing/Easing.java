package com.github.andrew0030.pandora_core.utils.easing;

import net.minecraft.util.Mth;

import java.util.function.IntPredicate;

public enum Easing {
    LINEAR((value) -> value),
    /** <b>STEPS</b> requires an int value to specify the step count. */
    STEPS((EasingFunctionWithInt) (value, intData) -> {
        if (value < 0) return 0;
        if (intData < 2) intData = 2;

        float stepLength = 1.0f / intData;
        float result = (intData - 1) * stepLength;
        if (value > result) return result;

        IntPredicate isTargetBeforeOrAt = i -> value < i * stepLength;
        int stepIndex = Mth.binarySearch(0, Math.round(intData) - 1, isTargetBeforeOrAt);
        if (stepIndex < 0)
            stepIndex = -(stepIndex + 1);
        return (stepIndex - 1) * stepLength;
    }),
    // Sine Easing
    SINE_IN((value) -> 1 - Mth.cos((value * Mth.PI) / 2)),
    SINE_OUT((value) -> Mth.sin((value * Mth.PI) / 2)),
    SINE_IN_OUT((value) -> -(Mth.cos(Mth.PI * value) - 1) / 2),
    // Quadratic Easing
    QUAD_IN((value) -> value * value),
    QUAD_OUT((value) -> 1 - (1 - value) * (1 - value)),
    QUAD_IN_OUT((value) -> value < 0.5 ? 2 * value * value : 1 - (float) Math.pow(-2 * value + 2, 2) / 2),
    // Cubic Easing
    CUBIC_IN((value) -> value * value * value),
    CUBIC_OUT((value) -> 1 - (float) Math.pow(1 - value, 3)),
    CUBIC_IN_OUT((value) -> value < 0.5 ? 4 * value * value * value : 1 - (float) Math.pow(-2 * value + 2, 3) / 2),
    // Quartic Easing
    QUART_IN((value) -> value * value * value * value),
    QUART_OUT((value) -> 1 - (float) Math.pow(1 - value, 4)),
    QUART_IN_OUT((value) -> value < 0.5 ? 8 * value * value * value * value : 1 - (float) Math.pow(-2 * value + 2, 4) / 2),
    // Quintic Easing
    QUINT_IN((value) -> value * value * value * value * value),
    QUINT_OUT((value) -> 1 - (float) Math.pow(1 - value, 5)),
    QUINT_IN_OUT((value) -> value < 0.5 ? 16 * value * value * value * value * value : 1 - (float) Math.pow(-2 * value + 2, 5) / 2),
    // Exponential Easing
    EXPO_IN((value) -> value == 0 ? 0 : (float) Math.pow(2, 10 * value - 10)),
    EXPO_OUT((value) -> value == 1 ? 1 : 1 - (float) Math.pow(2, -10 * value)),
    EXPO_IN_OUT((value) -> value == 0 ? 0 : value == 1 ? 1 : value < 0.5 ? (float) Math.pow(2, 20 * value - 10) / 2 : (2 - (float) Math.pow(2, -20 * value + 10)) / 2),
    // Circular Easing
    CIRC_IN((value) -> 1 - Mth.sqrt(1 - (float) Math.pow(value, 2))),
    CIRC_OUT((value) -> Mth.sqrt(1 - (float) Math.pow(value - 1, 2))),
    CIRC_IN_OUT((value) -> value < 0.5 ? (1 - Mth.sqrt(1 - (float) Math.pow(2 * value, 2))) / 2 : (Mth.sqrt(1 - (float) Math.pow(-2 * value + 2, 2)) + 1) / 2),
    // Back Easing
    /** <b>BACK_IN</b> requires a float value to specify the overshoot. */
    BACK_IN((EasingFunctionWithFloat) (value, floatData) -> ((1.70158F * floatData) + 1) * value * value * value - (1.70158F * floatData) * value * value),
    /** <b>BACK_OUT</b> requires a float value to specify the overshoot. */
    BACK_OUT((EasingFunctionWithFloat) (value, floatData) -> 1 + ((1.70158F * floatData) + 1) * (float) Math.pow(value - 1, 3) + (1.70158F * floatData) * (float) Math.pow(value - 1, 2)),
    /** <b>BACK_IN_OUT</b> requires a float value to specify the overshoot. */
    BACK_IN_OUT((EasingFunctionWithFloat) (value, floatData) -> value < 0.5 ? ((float) Math.pow(2 * value, 2) * (((1.70158F * floatData) + 1) * 2 * value - (1.70158F * floatData))) / 2 : ((float) Math.pow(2 * value - 2, 2) * (((1.70158F * floatData) + 1) * (value * 2 - 2) + (1.70158F * floatData)) + 2) / 2),
    // Elastic Easing
    /** <b>ELASTIC_IN</b> requires a float value to specify the bounciness. */
    ELASTIC_IN((EasingFunctionWithFloat) (value, floatData) -> 1 - (float) Math.pow(Mth.cos(value * Mth.PI / 2), 3) * Mth.cos(value * Mth.PI * floatData)),
    /** <b>ELASTIC_OUT</b> requires a float value to specify the bounciness. */
    ELASTIC_OUT((EasingFunctionWithFloat) (value, floatData) -> 1 - (1 - (float) Math.pow(Mth.cos((1 - value) * Mth.PI / 2), 3) * Mth.cos((1 - value) * Mth.PI * floatData))),
    /** <b>ELASTIC_IN_OUT</b> requires a float value to specify the bounciness. */
    ELASTIC_IN_OUT((EasingFunctionWithFloat) (value, floatData) -> value < 0.5F ? (1 - (float) Math.pow(Mth.cos(value * Mth.PI), 3) * Mth.cos(value * Mth.PI * floatData)) / 2F : 1 - (1 - (float) Math.pow(Mth.cos((1 - value) * Mth.PI), 3) * Mth.cos((1 - value) * Mth.PI * floatData)) / 2F),
    // Bounce Easing
    /** <b>BOUNCE_OUT</b> requires a float value to specify the bounciness. */
    BOUNCE_OUT((EasingFunctionWithFloat) (value, floatData) -> {
        if (value < 1 / 2.75F) {
            return 7.5625F * value * value;
        } else if (value < 2 / 2.75F) {
            return 30.25F * floatData * (float) Math.pow(value - 6F / 11F, 2) + 1 - floatData;
        } else if (value < 2.5 / 2.75F) {
            return 121 * floatData * floatData * (float) Math.pow(value - 9F / 11F, 2) + 1 - floatData * floatData;
        } else {
            return 484 * floatData * floatData * floatData * (float) Math.pow(value - 10.5F / 11F, 2) + 1 - floatData * floatData * floatData;
        }
    }),
    /** <b>BOUNCE_IN</b> requires a float value to specify the bounciness. */
    BOUNCE_IN((EasingFunctionWithFloat) (value, floatData) -> 1 - Easing.BOUNCE_OUT.setFloatValue(floatData).apply(1 - value)),
    /** <b>BOUNCE_IN_OUT</b> requires a float value to specify the bounciness. */
    BOUNCE_IN_OUT((EasingFunctionWithFloat) (value, floatData) -> value < 0.5
            ? (1 - Easing.BOUNCE_OUT.setFloatValue(floatData).apply(1 - 2 * value)) / 2
            : (1 + Easing.BOUNCE_OUT.setFloatValue(floatData).apply(2 * value - 1)) / 2);

    private final EasingFunction function;
    private final EasingFunctionWithInt functionWithInt;
    private final EasingFunctionWithFloat functionWithFloat;
    private int intValue;
    private float floatValue;

    Easing(EasingFunction function) {
        this.function = function;
        this.functionWithInt = null;
        this.functionWithFloat = null;
    }

    Easing(EasingFunctionWithInt functionWithInt) {
        this.function = null;
        this.functionWithInt = functionWithInt;
        this.functionWithFloat = null;
    }

    Easing(EasingFunctionWithFloat functionWithFloat) {
        this.function = null;
        this.functionWithInt = null;
        this.functionWithFloat = functionWithFloat;
    }

    public Easing setIntValue(int value) {
        this.intValue = value;
        return this;
    }

    public Easing setFloatValue(float value) {
        this.floatValue = value;
        return this;
    }

    public float apply(float value) {
        if (this.function != null) {
            return this.function.apply(value);
        } else if (this.functionWithInt != null) {
            return this.functionWithInt.apply(value, this.intValue);
        } else if (this.functionWithFloat != null) {
            return this.functionWithFloat.apply(value, this.floatValue);
        } else {
            throw new UnsupportedOperationException("The " + this.name() + " easing function does not support the provided parameters.");
        }
    }
}
package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.function.Predicate;

public class FloatConfigConverter implements IPaCoConfigConverter<Float, Number> {
    private final float min;
    private final float max;

    public FloatConfigConverter(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Number serialize(Float value) {
        return value;
    }

    @Override
    public Float deserialize(Number value) {
        return value.floatValue();
    }

    @Override
    public Class<Number> getSerializedType() {
        return Number.class;
    }

    @Override
    public Class<Float> getDeserializedType() {
        return Float.class;
    }

    @Override
    public Predicate<Number> getSerializedPredicate() {
        return number -> {
            if (number == null) return false;
            float floatValue = number.floatValue();
            return floatValue >= this.min && floatValue <= this.max;
        };
    }
}
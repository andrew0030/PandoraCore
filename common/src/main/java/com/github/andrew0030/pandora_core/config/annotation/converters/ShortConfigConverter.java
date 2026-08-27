package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.function.Predicate;

public class ShortConfigConverter implements IPaCoConfigConverter<Short, Number> {
    private final short min;
    private final short max;

    public ShortConfigConverter(short min, short max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Number serialize(Short value) {
        return value;
    }

    @Override
    public Short deserialize(Number value) {
        return value.shortValue();
    }

    @Override
    public Class<Number> getSerializedType() {
        return Number.class;
    }

    @Override
    public Class<Short> getDeserializedType() {
        return Short.class;
    }

    @Override
    public Predicate<Number> getSerializedPredicate() {
        return number -> {
            if (number == null) return false;
            short shortValue = number.shortValue();
            return shortValue >= this.min && shortValue <= this.max;
        };
    }
}
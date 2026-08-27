package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.function.Predicate;

public class LongConfigConverter implements IPaCoConfigConverter<Long, Number> {
    private final long min;
    private final long max;

    public LongConfigConverter(long min, long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Number serialize(Long value) {
        return value;
    }

    @Override
    public Long deserialize(Number value) {
        return value.longValue();
    }

    @Override
    public Class<Number> getSerializedType() {
        return Number.class;
    }

    @Override
    public Class<Long> getDeserializedType() {
        return Long.class;
    }

    @Override
    public Predicate<Number> getSerializedPredicate() {
        return number -> {
            if (number == null) return false;
            long longValue = number.longValue();
            return longValue >= this.min && longValue <= this.max;
        };
    }
}
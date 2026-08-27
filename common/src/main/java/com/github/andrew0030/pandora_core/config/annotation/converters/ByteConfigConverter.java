package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.function.Predicate;

public class ByteConfigConverter implements IPaCoConfigConverter<Byte, Number> {
    private final byte min;
    private final byte max;

    public ByteConfigConverter(byte min, byte max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Number serialize(Byte value) {
        return value;
    }

    @Override
    public Byte deserialize(Number value) {
        return value.byteValue();
    }

    @Override
    public Class<Number> getSerializedType() {
        return Number.class;
    }

    @Override
    public Class<Byte> getDeserializedType() {
        return Byte.class;
    }

    @Override
    public Predicate<Number> getSerializedPredicate() {
        return number -> {
            if (number == null) return false;
            byte byteValue = number.byteValue();
            return byteValue >= this.min && byteValue <= this.max;
        };
    }
}
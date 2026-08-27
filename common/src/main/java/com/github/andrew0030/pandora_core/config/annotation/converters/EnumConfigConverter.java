package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.function.Predicate;

public class EnumConfigConverter<E extends Enum<E>> implements IPaCoConfigConverter<E, String> {
    private final Class<E> enumType;

    public EnumConfigConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public String serialize(E value) {
        return value.name();
    }

    @Override
    public E deserialize(String value) {
        return Enum.valueOf(this.enumType, value);
    }

    @Override
    public Class<String> getSerializedType() {
        return String.class;
    }

    @Override
    public Class<E> getDeserializedType() {
        return this.enumType;
    }

    @Override
    public Predicate<String> getSerializedPredicate() {
        return string -> {
            if (string == null) return false;
            try {
                Enum.valueOf(this.enumType, string);
                return true;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        };
    }
}
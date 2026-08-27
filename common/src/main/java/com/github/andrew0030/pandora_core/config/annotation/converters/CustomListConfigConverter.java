package com.github.andrew0030.pandora_core.config.annotation.converters;

import com.github.andrew0030.pandora_core.config.manager.IPaCoConfigConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CustomListConfigConverter<T, R> implements IPaCoConfigConverter<List<T>, List<R>> {
    private final IPaCoConfigConverter<T, R> elementConverter;

    public CustomListConfigConverter(IPaCoConfigConverter<T, R> elementConverter) {
        this.elementConverter = elementConverter;
    }

    @Override
    public List<R> serialize(List<T> value) {
        if (value == null) return null;
        List<R> serialized = new ArrayList<>(value.size());
        for (T element : value)
            serialized.add(elementConverter.serialize(element));
        return serialized;
    }

    @Override
    public List<T> deserialize(List<R> value) {
        if (value == null) return null;
        List<T> deserialized = new ArrayList<>(value.size());
        for (R element : value)
            deserialized.add(elementConverter.deserialize(element));
        return deserialized;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<R>> getSerializedType() {
        // Because of type erasure we cant really do better than List at runtime
        return (Class<List<R>>) (Class<?>) List.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<T>> getDeserializedType() {
        // Because of type erasure we cant really do better than List at runtime
        return (Class<List<T>>) (Class<?>) List.class;
    }

    @Override
    public Predicate<List<R>> getSerializedPredicate() {
        return list -> {
            Class<R> expectedElementType = this.elementConverter.getSerializedType();
            Predicate<R> elementPredicate = this.elementConverter.getSerializedPredicate();
            for (R element : list) {
                if (!expectedElementType.isInstance(element)) return false;
                if (elementPredicate != null && !elementPredicate.test(element)) return false;
            }
            return true;
        };
    }
}

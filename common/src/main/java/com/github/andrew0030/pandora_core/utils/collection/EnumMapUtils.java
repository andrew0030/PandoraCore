package com.github.andrew0030.pandora_core.utils.collection;

import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility class for creating and populating {@link EnumMap} instances in a concise and type-safe manner.
 * <p>
 * This is especially useful when initializing static final {@link EnumMap EnumMaps} inline, which otherwise
 * require verbose or multi-step construction.
 */
public class EnumMapUtils {

    /**
     * Creates an {@link EnumMap} with the specified entries.
     *
     * @param keyType The {@link Class} of the enum key type.
     * @param entries The entries to populate the map with.
     * @return A populated {@link EnumMap} instance.
     */
    @SafeVarargs
    public static <K extends Enum<K>, V> EnumMap<K, V> enumMap(Class<K> keyType, Map.Entry<K, V>... entries) {
        EnumMap<K, V> map = new EnumMap<>(keyType);
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * Convenience method for creating a {@link Map.Entry} for use with {@link EnumMapUtils#enumMap}.
     *
     * @param key   The key of the entry.
     * @param value The value of the entry.
     * @return A {@link Map.Entry} representing the key-value pair.
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}
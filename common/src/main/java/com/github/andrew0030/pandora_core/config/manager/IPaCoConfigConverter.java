package com.github.andrew0030.pandora_core.config.manager;

import java.util.function.Predicate;

/**
 * Defines a converter used to serialize and deserialize custom configuration value types.
 *
 * <p>This interface allows complex or non-native Java objects to be stored in config
 * files by converting them into a serialized representation supported by night-config
 * (e.g. {@link String}, {@link Integer}, {@link Short}, and so on).</p>
 *
 * <p>The type parameters represent:</p>
 * <ul>
 *   <li><b>T</b> – The in-memory object type used by the application.</li>
 *   <li><b>R</b> – The serialized representation type that will be written to
 *   and read from the config file.</li>
 * </ul>
 *
 * @implNote Implementations are responsible for providing a reversible transformation
 * between these two types.
 * @param <T> The deserialized object type used at runtime
 * @param <R> The serialized representation type stored in the config file
 */
public interface IPaCoConfigConverter<T, R> {
    /**
     * Serializes a runtime object into the corresponding configuration value.
     *
     * @param value The deserialized configuration value
     * @return The serialized representation of this object
     */
    R serialize(T value);

    /**
     * Deserializes a configuration value into the corresponding runtime object.
     *
     * @param value The serialized configuration value
     * @return The reconstructed runtime object
     */
    T deserialize(R value);

    /**
     * Returns the type used to represent the serialized form of this
     * object in the config.
     *
     * @return The class of the serialized representation type
     */
    Class<R> getSerializedType();

    /**
     * Returns the type used to represent the deserialized form of this
     * object during runtime.
     *
     * @return The class of the deserialized representation type
     */
    Class<T> getDeserializedType();

    /**
     * Returns a predicate used to validate serialized values before they are
     * accepted by the configuration system.
     *
     * <p>This allows implementations to enforce constraints on configuration
     * values (for example validating formats, ranges, or patterns).</p>
     *
     * @return A predicate used to validate serialized configuration values
     */
    Predicate<R> getSerializedPredicate();
}
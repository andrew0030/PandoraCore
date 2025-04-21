package com.github.andrew0030.pandora_core.utils.function;

import java.util.function.Supplier;

/**
 * A {@code Supplier} that is initially unset and can be assigned a value once.
 * This is useful for scenarios where a {@link Supplier} reference is returned
 * before the actual object is known, such as in deferred registration systems.
 * <p>
 * The value must be explicitly set exactly once using {@link #set(Object)}.
 * Once set, calls to {@link #get()} will return that value.
 * </p>
 * <p>
 * Attempting to call {@link #get()} before the value has been set,
 * or calling {@link #set(Object)} more than once, will result in an exception.
 * </p>
 *
 * @param <T> the type of results supplied by this supplier
 */
public class LazySupplier<T> implements Supplier<T> {
    private T value;

    /**
     * Sets the value to be returned by this supplier.
     * This method may only be called once.
     *
     * @param value the value to assign
     * @throws IllegalStateException if the value has already been set
     */
    public void set(T value) {
        if (this.value != null)
            throw new IllegalStateException("Value has already been set!");
        this.value = value;
    }

    /**
     * Gets the value supplied by this supplier.
     *
     * @return the previously set value
     * @throws IllegalStateException if the value has not been set yet
     */
    @Override
    public T get() {
        if (this.value == null)
            throw new IllegalStateException("Value has not been set yet!");
        return this.value;
    }
}
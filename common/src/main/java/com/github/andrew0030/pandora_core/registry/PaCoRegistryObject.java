package com.github.andrew0030.pandora_core.registry;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Internal representation of a registered game object (e.g., Block, Item, etc.).
 *
 * <p>Instances of this class are created and managed by {@link PaCoRegistry} and
 * should not be constructed manually by API users. This class acts as a wrapper
 * that stores the object’s factory used to create it. It lazily initializes the
 * object when {@link #get()} is first called.</p>
 *
 * @param <T> The type of object being registered.
 */
@ApiStatus.Internal
public final class PaCoRegistryObject<T> implements Supplier<T> {
    private final Supplier<T> factory;
    private T instance;

    /**
     * Constructs a new registry object.
     * @param factory A supplier that creates the object.
     */
    PaCoRegistryObject(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * Lazily creates and returns the instance of the registered object.
     * @return The registered object instance.
     */
    @Override
    public T get() {
        if (this.instance == null)
            this.instance = this.factory.get();
        return this.instance;
    }
}
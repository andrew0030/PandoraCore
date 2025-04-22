package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.core.Registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple, cross-platform registry wrapper used for registering game objects (Items, Blocks, etc.)
 * in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoRegistry<Item> ITEMS = new PaCoRegistry<>(BuiltInRegistries.ITEM, MyMod.MOD_ID);
 * public static final Supplier<Item> EXAMPLE_ITEM = ITEMS.add("example_item", () -> new Item(new Item.Properties()));
 * }</pre>
 * <p>And then during mod construction:</p>
 * <pre>{@code
 * ExampleModItems.ITEMS.register();
 * }</pre>
 *
 * @param <T> The type of object being registered (e.g., Item, Block, EntityType, etc.)
 */
public class PaCoRegistry<T> {
    private final Registry<T> registry;
    private final String modId;
    private final Map<String, PaCoRegistryObject<T>> registryQueue = new LinkedHashMap<>();

    /**
     * Constructs a new registry helper for a given registry and mod id.
     * @param registry The registry this helper will register objects into.
     * @param modId    The mod id namespace to register objects under.
     */
    public PaCoRegistry(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    /**
     * Adds a new object to be registered later.
     * @param name    The name (path) of the object.
     * @param factory A supplier that creates the object when registration occurs.
     * @return The same supplier, for convenient assignment.
     */
    public Supplier<T> add(String name, Supplier<T> factory) {
        if (this.registryQueue.containsKey(name))
            throw new IllegalArgumentException("An object with the name '" + name + "' is already registered.");
        var registryObject = new PaCoRegistryObject<>(factory);
        this.registryQueue.put(name, registryObject);
        return registryObject;
    }

    /**
     * This needs to be called, to register all added entries.
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside ModInitializer#onInitialize.<br/><br/>
     * <strong>NOTE</strong>: This method also clears {@link PaCoRegistry#registryQueue} after the objects have been registered.
     */
    public void register() {
        Services.REGISTRY.register(this.registry, this.modId, this.registryQueue);
        // If there is a random mod that rebuilds registries, this will probably conflict with it.
        // That said there is probably VERY FEW mods that do this, if any...
        this.registryQueue.clear();
    }
}
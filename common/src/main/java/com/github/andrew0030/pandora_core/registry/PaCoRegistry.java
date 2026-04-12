package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
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
    private final PaCoRegistryBuilder.SimpleSpec<T> spec;
    private final ResourceKey<? extends Registry<T>> key;
    private final String modId;
    private final Map<String, PaCoRegistryObject<T>> registryQueue = new LinkedHashMap<>();

    /**
     * Constructs a new registry helper for a given {@link Registry} and {@code modId}.
     *
     * @param registry The {@link Registry} this helper will register objects into.
     * @param modId    The namespace to register objects under.
     */
    public PaCoRegistry(Registry<T> registry, String modId) {
        this.registry = registry;
        this.spec = null;
        this.key = registry.key();
        this.modId = modId;
    }

    /**
     * Constructs a new registry helper for a given {@link PaCoRegistryBuilder.SimpleSpec} and {@code modId}.
     *
     * @param spec     The {@code builder} used to construct the registry, use {@link PaCoRegistryBuilder#simple(ResourceKey)}
     * @param modId    The namespace to register objects under
     */
    public PaCoRegistry(PaCoRegistryBuilder.SimpleSpec<T> spec, String modId) {
        this.registry = null;
        this.spec = spec;
        this.key = spec.getResourceKey();
        this.modId = modId;
    }

    /**
     * Adds a new object to be registered later.
     * @param name    The name (path) of the object.
     * @param factory A supplier that creates the object when registration occurs.
     * @return The same supplier, for convenient assignment.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> Supplier<U> add(String name, Supplier<U> factory) {
        if (this.registryQueue.containsKey(name))
            throw new IllegalArgumentException("An object with the name '" + name + "' is already registered.");
        var registryObject = new PaCoRegistryObject<>(factory);
        // Note: We're storing the supplier as PaCoRegistryObject<T>, so we need to cast.
        // The cast to PaCoRegistryObject<T> is safe because U extends T.
        this.registryQueue.put(name, (PaCoRegistryObject<T>) registryObject);
        return registryObject;
    }

    /**
     * This needs to be called, to register all added entries.<br/>
     * Here is a list of when to call it, on each loader:
     * <br/><br/>
     * <strong>Forge</strong>: Inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.
     */
    public void register() {
        if (this.registry != null) {
            Services.REGISTRY.register(this.registry, this.modId, this.registryQueue);
        } else {
            Services.REGISTRY.registerCustom(this.spec, this.modId, this.registryQueue);
        }
    }

    /**
     * Registers a dynamic (data pack) registry using the provided {@link PaCoRegistryBuilder.DynamicSpec}.
     * <br/><br/>
     * <strong>Note:</strong> this is a <em>utility</em> method for registering dynamic registries, that are
     * <strong>data-driven</strong> and do not use the standard {@code add()}/{@code register()} structure.<br/>
     *
     * <p><strong>Usage example:</strong> Call this method directly during mod construction</p>
     * <pre>{@code
     * PaCoRegistry.registerDynamic(PaCoRegistryBuilder.dynamic(ExampleRegistryKeys.EXAMPLE_KEY, ExampleCodec.CODEC));
     * }</pre>
     *
     * @param spec The {@code builder} used to construct the dynamic registry, use {@link PaCoRegistryBuilder#dynamic(ResourceKey, Codec)}
     */
    public static <T> void registerDynamic(PaCoRegistryBuilder.DynamicSpec<T> spec) {
        Services.REGISTRY.registerDynamic(spec);
    }

    /** @return The registry's {@link ResourceKey} */
    public ResourceKey<? extends Registry<T>> getKey() {
        return this.key;
    }

    /**
     * Gets the actual {@link Registry} instance for this registry helper.
     * <br/><br/>
     * <strong>Note:</strong> This method should only be called <em>after</em> the registry has been
     * registered. Calling this before registration, will result in a {@link NullPointerException}.
     *
     * @return The registered {@link Registry} instance.
     * @throws NullPointerException if the registry has not been registered yet.
     */
    @SuppressWarnings("unchecked")
    public Registry<T> getRegistry() {
        ResourceLocation location = this.key.location();
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(location);
        if (registry == null)
            throw new NullPointerException(String.format("Attempted to get '%s' registry, before it was registered!", location));
        return registry;
    }

    /**
     * Returns an unmodifiable collection, of all entries queued for registration in this registry helper.
     * <p>This is useful for iteration or inspection of registered objects after the registration phase.</p>
     *
     * @return An unmodifiable {@link Collection} of {@link PaCoRegistryObject PaCoRegistryObjects}
     */
    public Collection<PaCoRegistryObject<T>> getEntries() {
        return Collections.unmodifiableCollection(this.registryQueue.values());
    }
}
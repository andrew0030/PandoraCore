package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.client.render.dynamic.PaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.item.IPaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaCoRendererRegistry {
    private static final Map<Item, PaCoRegistryObject<PaCoDynamicItemRenderer>> RENDERERS = new HashMap<>();

    // #################################################################################################################
    // #                                                     Items                                                     #
    // #################################################################################################################
    /**
     * Registers a {@link PaCoDynamicItemRenderer} for the specified {@link ItemLike}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param item     The {@link ItemLike} to associate with the dynamic renderer
     * @param renderer The {@link Function} used to initialize the dynamic renderer
     */
    public static void register(ItemLike item, Function<Minecraft, PaCoDynamicItemRenderer> renderer) {
        Objects.requireNonNull(item.asItem(), "item is null");
        Objects.requireNonNull(renderer, "renderer is null");
        if (RENDERERS.putIfAbsent(item.asItem(), new PaCoRegistryObject<>(() -> renderer.apply(Minecraft.getInstance()))) != null)
            throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " already has a dynamic renderer attached!");
    }

    /**
     * Registers a {@link PaCoDynamicItemRenderer} for the specified {@link ItemLike}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param item     The {@link ItemLike} to associate with the dynamic renderer
     * @param renderer The {@link BiFunction} used to initialize the dynamic renderer
     */
    public static void register(ItemLike item, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, PaCoDynamicItemRenderer> renderer) {
        Objects.requireNonNull(renderer, "renderer is null");
        PaCoRendererRegistry.register(item, minecraft -> renderer.apply(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()));
    }

    /**
     * Registers a {@link PaCoDynamicItemRenderer} for all the specified {@link ItemLike ItemLikes}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param items    A {@link Set} containing {@link ItemLike} instances, to associate with the dynamic renderer
     * @param renderer The {@link Function} used to initialize the dynamic renderer
     */
    public static void register(Set<ItemLike> items, Function<Minecraft, PaCoDynamicItemRenderer> renderer) {
        Objects.requireNonNull(items, "items is null");
        Objects.requireNonNull(renderer, "renderer is null");
        if (items.isEmpty()) throw new IllegalArgumentException("items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "items contains null element");
            Objects.requireNonNull(item.asItem(), "item.asItem() is null for element in set");
        }
        // Creates a single PaCoRegistryObject instance that will be shared by all ItemLike instances in the given Set
        PaCoRegistryObject<PaCoDynamicItemRenderer> registryObject = new PaCoRegistryObject<>(() -> renderer.apply(Minecraft.getInstance()));
        for (ItemLike item : items)
            if (RENDERERS.putIfAbsent(item.asItem(), registryObject) != null)
                throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " already has a dynamic renderer attached!");
    }

    /**
     * Registers a {@link PaCoDynamicItemRenderer} for all the specified {@link ItemLike ItemLikes}.
     *
     * <p>Here is a list of when to call it, on each loader:</p>
     * <p><strong>Forge</strong>: Inside {@code FMLClientSetupEvent}.</p>
     * <p><strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.</p>
     *
     * @param items    A {@link Set} containing {@link ItemLike} instances, to associate with the dynamic renderer
     * @param renderer The {@link BiFunction} used to initialize the dynamic renderer
     */
    public static void register(Set<ItemLike> items, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, PaCoDynamicItemRenderer> renderer) {
        Objects.requireNonNull(renderer, "renderer is null");
        PaCoRendererRegistry.register(items, minecraft -> renderer.apply(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()));
    }

    /**
     * Used to retrieve the {@link PaCoRegistryObject} containing the {@link PaCoDynamicItemRenderer}
     * instance, that corresponds to the given {@link ItemLike}.
     * <p>
     * <strong>NOTE:</strong> Avoid calling this method when possible, instead call {@link #getRenderer(ItemLike)}</p>
     */
    @Deprecated(forRemoval = false)
    public static PaCoRegistryObject<PaCoDynamicItemRenderer> getRendererRegistry(ItemLike item) {
        Objects.requireNonNull(item, "item is null");
        return RENDERERS.get(item.asItem());
    }

    /** @return The {@link PaCoDynamicItemRenderer} attached to the given {@link ItemLike}, or {@code null} if none exists. */
    public static PaCoDynamicItemRenderer getRenderer(ItemLike item) {
        Objects.requireNonNull(item.asItem(), "item is null");
        return ((IPaCoDynamicItemRenderer) item.asItem()).pandoraCore$getDynamicItemRenderer();
    }

    // #################################################################################################################
    // #                                                Internal Stuff                                                 #
    // #################################################################################################################
    /**
     * <strong>DO NOT USE</strong>
     * <p>Use the other methods in this class!</p>
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void registerInternal() {
        RENDERERS.forEach((type, factory) -> ((IPaCoDynamicItemRenderer) type).pandoraCore$setDynamicItemRenderer(factory.get()));
    }
}
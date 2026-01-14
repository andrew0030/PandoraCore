package com.github.andrew0030.pandora_core.client.render;

import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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

@ApiStatus.Internal
public class BEWLRManager {
    public static final Map<Item, PaCoRegistryObject<BlockEntityWithoutLevelRenderer>> RENDERERS = new HashMap<>();

    // Registers a single "ItemLike" instance to a single "BlockEntityWithoutLevelRenderer" instance, and provides "Minecraft".
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void register(ItemLike item, Function<Minecraft, BlockEntityWithoutLevelRenderer> renderer) {
        Objects.requireNonNull(item.asItem(), "item is null");
        Objects.requireNonNull(renderer, "renderer is null");

        if (BEWLRManager.RENDERERS.putIfAbsent(item.asItem(), new PaCoRegistryObject<>(() -> renderer.apply(Minecraft.getInstance()))) != null)
            throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " is already registered as a BEWLR!");
    }

    // Registers a single "ItemLike" instance to a single "BlockEntityWithoutLevelRenderer" instance, and provides "BlockEntityRenderDispatcher" and "EntityModelSet".
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void register(ItemLike item, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer) {
        Objects.requireNonNull(renderer, "renderer is null");
        BEWLRManager.register(item, minecraft -> renderer.apply(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()));
    }

    // Registers a set of "ItemLike" instances to a single "BlockEntityWithoutLevelRenderer" instance, and provides "Minecraft".
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void register(Set<ItemLike> items, Function<Minecraft, BlockEntityWithoutLevelRenderer> renderer) {
        Objects.requireNonNull(items, "items is null");
        Objects.requireNonNull(renderer, "renderer is null");
        if (items.isEmpty()) throw new IllegalArgumentException("items is empty");

        for (ItemLike item : items) {
            Objects.requireNonNull(item, "items contains null element");
            Objects.requireNonNull(item.asItem(), "item.asItem() is null for element in set");
        }

        // Creates a single PaCoRegistryObject instance that will be shared by all ItemLike instances in the given Set
        PaCoRegistryObject<BlockEntityWithoutLevelRenderer> registryObject = new PaCoRegistryObject<>(() -> renderer.apply(Minecraft.getInstance()));
        for (ItemLike item : items)
            if (BEWLRManager.RENDERERS.putIfAbsent(item.asItem(), registryObject) != null)
                throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " is already registered as a BEWLR!");
    }

    // Registers a set of "ItemLike" instances to a single "BlockEntityWithoutLevelRenderer" instance, and provides "BlockEntityRenderDispatcher" and "EntityModelSet".
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void register(Set<ItemLike> items, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer) {
        Objects.requireNonNull(renderer, "renderer is null");
        BEWLRManager.register(items, minecraft -> renderer.apply(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()));
    }

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static PaCoRegistryObject<BlockEntityWithoutLevelRenderer> get(ItemLike item) {
        Objects.requireNonNull(item.asItem(), "item is null");

        return BEWLRManager.RENDERERS.get(item.asItem());
    }
}
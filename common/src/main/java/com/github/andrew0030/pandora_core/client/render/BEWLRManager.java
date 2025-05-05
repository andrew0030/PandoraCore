package com.github.andrew0030.pandora_core.client.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApiStatus.Internal
public class BEWLRManager {
    public static final Map<Item, BlockEntityWithoutLevelRenderer> RENDERERS = new HashMap<>();

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static void register(ItemLike item, BlockEntityWithoutLevelRenderer renderer) {
        Objects.requireNonNull(item.asItem(), "item is null");
        Objects.requireNonNull(renderer, "renderer is null");

        if (BEWLRManager.RENDERERS.putIfAbsent(item.asItem(), renderer) != null)
            throw new IllegalArgumentException("Item " + BuiltInRegistries.ITEM.getKey(item.asItem()) + " is already registered as a BEWLR!");
    }

    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static BlockEntityWithoutLevelRenderer get(ItemLike item) {
        Objects.requireNonNull(item.asItem(), "item is null");

        return BEWLRManager.RENDERERS.get(item.asItem());
    }
}
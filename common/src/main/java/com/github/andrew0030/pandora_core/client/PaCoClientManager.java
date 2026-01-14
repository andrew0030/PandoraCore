package com.github.andrew0030.pandora_core.client;

import com.github.andrew0030.pandora_core.client.render.BEWLRManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.ItemLike;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaCoClientManager {
    /**
     * Registers a {@link BlockEntityWithoutLevelRenderer} (BEWLR) for the specified {@link ItemLike} object.
     *
     * <p><strong>Important:</strong> The item's JSON model must use the parent model {@code "builtin/entity"}
     * in order for the custom renderer to take effect.
     *
     * <p>Here is a list of when to call it, on each loader:<br/>
     * <strong>Forge</strong>: Inside FMLClientSetupEvent.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.
     *
     * @param item     The {@link ItemLike} to associate with the custom renderer. Must not be null.
     * @param renderer The {@link Function} used to initialize the renderer. Must not be null.
     *
     * @throws NullPointerException     If either {@code item} or {@code renderer} is null.
     * @throws IllegalArgumentException If the item already has a renderer registered.
     */
    public static void registerItemBEWLR(ItemLike item, Function<Minecraft, BlockEntityWithoutLevelRenderer> renderer) {
        BEWLRManager.register(item, renderer);
    }

    /**
     * Registers a {@link BlockEntityWithoutLevelRenderer} (BEWLR) for the specified {@link ItemLike} object.
     *
     * <p><strong>Important:</strong> The item's JSON model must use the parent model {@code "builtin/entity"}
     * in order for the custom renderer to take effect.
     *
     * <p>Here is a list of when to call it, on each loader:<br/>
     * <strong>Forge</strong>: Inside FMLClientSetupEvent.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.
     *
     * @param item     The {@link ItemLike} to associate with the custom renderer. Must not be null.
     * @param renderer The {@link BiFunction} used to initialize the renderer. Must not be null.
     *
     * @throws NullPointerException     If either {@code item} or {@code renderer} is null.
     * @throws IllegalArgumentException If the item already has a renderer registered.
     */
    public static void registerItemBEWLR(ItemLike item, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer) {
        BEWLRManager.register(item, renderer);
    }

    /**
     * Registers a {@link BlockEntityWithoutLevelRenderer} (BEWLR) for the specified {@link ItemLike} objects.
     *
     * <p><strong>Important:</strong> The item's JSON models must use the parent model {@code "builtin/entity"}
     * in order for the custom renderer to take effect.
     *
     * <p>Here is a list of when to call it, on each loader:<br/>
     * <strong>Forge</strong>: Inside FMLClientSetupEvent.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.
     *
     * @param items    A {@link Set} containing {@link ItemLike} instances, to associate with the custom renderer. Must not be null.
     * @param renderer The {@link Function} used to initialize the renderer. Must not be null.
     *
     * @throws NullPointerException     If either {@code items} or {@code renderer} is null.
     * @throws IllegalArgumentException If the items set is empty.
     * @throws IllegalArgumentException If an item already has a renderer registered.
     */
    public static void registerItemBEWLR(Set<ItemLike> items, Function<Minecraft, BlockEntityWithoutLevelRenderer> renderer) {
        BEWLRManager.register(items, renderer);
    }

    /**
     * Registers a {@link BlockEntityWithoutLevelRenderer} (BEWLR) for the specified {@link ItemLike} objects.
     *
     * <p><strong>Important:</strong> The item's JSON models must use the parent model {@code "builtin/entity"}
     * in order for the custom renderer to take effect.
     *
     * <p>Here is a list of when to call it, on each loader:<br/>
     * <strong>Forge</strong>: Inside FMLClientSetupEvent.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.
     *
     * @param items    A {@link Set} containing {@link ItemLike} instances, to associate with the custom renderer. Must not be null.
     * @param renderer The {@link BiFunction} used to initialize the renderer. Must not be null.
     *
     * @throws NullPointerException     If either {@code items} or {@code renderer} is null.
     * @throws IllegalArgumentException If the items set is empty.
     * @throws IllegalArgumentException If an item already has a renderer registered.
     */
    public static void registerItemBEWLR(Set<ItemLike> items, BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> renderer) {
        BEWLRManager.register(items, renderer);
    }
}
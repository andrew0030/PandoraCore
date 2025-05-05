package com.github.andrew0030.pandora_core.client;

import com.github.andrew0030.pandora_core.client.render.BEWLRManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.ItemLike;

public class PaCoClientManager {
    /**
     * Registers a {@link BlockEntityWithoutLevelRenderer} (BEWLR) for the specified {@link ItemLike}.
     *
     * <p><strong>Important:</strong> The item's JSON model must use the parent model {@code "builtin/entity"}
     * in order for the custom renderer to take effect.
     *
     * <p>Here is a list of when to call it, on each loader:<br/>
     * <strong>Forge</strong>: Inside FMLClientSetupEvent.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.
     *
     * @param item     The item to associate with the custom renderer. Must not be null.
     * @param renderer The renderer instance responsible for rendering the item. Must not be null.
     *
     * @throws NullPointerException     If either {@code item} or {@code renderer} is null.
     * @throws IllegalArgumentException If the item already has a renderer registered.
     */
    public static void registerItemBEWLR(ItemLike item, BlockEntityWithoutLevelRenderer renderer) {
        BEWLRManager.register(item, renderer);
    }
}
package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

/**
 * This class provides a simple API for adding custom entries to existing creative mode tabs, along
 * with optional control over insertion order and visibility. It also handles the sorting of insertions
 * based on dependencies between inserted items.
 * <p>
 * It also includes a {@link PaCoTabManager#builder()} method, which can be used inside
 * the common module to easily get access to a {@link CreativeModeTab.Builder}.
 * </p>
 */
public class PaCoTabManager {
    /**
     * A quality of life method that allows retrieving a creative
     * mode tab builder in the common module more easily.
     *
     * @return A new {@link CreativeModeTab.Builder}
     */
    public static CreativeModeTab.Builder builder() {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0);
    }

    /**
     * This method can be used to insert items into an existing {@link CreativeModeTab}.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * PaCoTabManager.insertionBuilder(CreativeModeTabs.REDSTONE_BLOCKS)
     *     .add(new ItemStack(ExItems.FUNKY.get()), new ItemStack(ExBlocks.TEST.get()))
     *     .insertBefore(Items.REDSTONE_TORCH)
     *     .apply();
     * }</pre>
     *
     * @param tab A {@link ResourceKey<CreativeModeTab>} pointing to the {@link CreativeModeTab}, items will be inserted into.
     * @return A new {@link TabInsertionBuilder} instance.
     */
    public static TabInsertionBuilder insertionBuilder(ResourceKey<CreativeModeTab> tab) {
        return new TabInsertionBuilder(tab);
    }
}
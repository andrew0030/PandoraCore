package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaCoTabManager {
    static final List<TabInsertion> TAB_INSERTIONS = new ArrayList<>();

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

    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    public static List<TabInsertion> getInsertionsFor(ResourceKey<CreativeModeTab> tab) {
        return TAB_INSERTIONS.stream()
                .filter(tabInsertion -> tabInsertion.getTab().equals(tab))
                .collect(Collectors.toList());
    }
}
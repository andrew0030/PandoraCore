package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Use {@link PaCoTabManager#insertionBuilder(ResourceKey)} to create a {@link TabInsertion}.
 */
public class TabInsertion {
    private final ResourceKey<CreativeModeTab> tab;
    private final Action action;
    private final TabVisibility visibility;

    TabInsertion(ResourceKey<CreativeModeTab> tab, Action action, TabVisibility visibility) {
        this.tab = tab;
        this.action = action;
        this.visibility = visibility;
    }

    public ResourceKey<CreativeModeTab> getTab() {
        return this.tab;
    }

    public Action getAction() {
        return this.action;
    }

    public TabVisibility getVisibility() {
        return this.visibility;
    }

    @FunctionalInterface
    public interface Action {
        void apply(List<ItemStack> list);
    }
}
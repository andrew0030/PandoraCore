package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Use {@link PaCoTabManager#insertionBuilder(ResourceKey)} to create a {@link TabInsertion}.
 */
public class TabInsertion {
    private final ResourceKey<CreativeModeTab> tab;
    private final Action action;
    private final TabVisibility visibility;
    @Nullable private final ItemStack target;
    private final List<ItemStack> insertedStacks;

    TabInsertion(ResourceKey<CreativeModeTab> tab, Action action, TabVisibility visibility, ItemStack target, List<ItemStack> insertedStacks) {
        this.tab = tab;
        this.action = action;
        this.visibility = visibility;
        this.target = target;
        this.insertedStacks = insertedStacks;
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

    @Nullable
    public ItemStack getTarget() {
        return this.target;
    }

    public boolean hasTarget() {
        return this.target != null;
    }

    public List<ItemStack> getInsertedStacks() {
        return this.insertedStacks;
    }

    /**
     * Helper method to easily check if the {@link TabInsertion} is
     * inserting the given {@link ItemStack} into a tab.
     *
     * @param target The {@link ItemStack} we are checking for.
     * @return Whether the {@link TabInsertion} is inserting the given {@link ItemStack}.
     */
    public boolean isInserting(ItemStack target) {
        // If the target is null we can quickly say that "no this insertion does not have that target"
        if (target == null) return false;
        // If the target wasn't null we go through the inserted stacks and compare them with the target
        for (ItemStack stack : this.insertedStacks) {
            if (target.hasTag()) {
                if (ItemStack.matches(stack, target))
                    return true;
            } else {
                if (stack.is(target.getItem()))
                    return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    public interface Action {
        void apply(List<ItemStack> list);
    }
}
package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Use {@link PaCoTabManager#insertionBuilder(ResourceKey)} to create a {@link TabInsertion}.
 */
public class TabInsertion {
    private final List<ItemStack> stacks;
    private final TabVisibility visibility;
    @Nullable private final ItemStack target;
    private final boolean insertBefore;
    private final boolean targetsInsertion;

    TabInsertion(List<ItemStack> stacks, TabVisibility visibility, @Nullable ItemStack target, boolean insertBefore, boolean targetsInsertion) {
        this.stacks = stacks;
        this.visibility = visibility;
        this.target = target;
        this.insertBefore = insertBefore;
        this.targetsInsertion = targetsInsertion;
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
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

    public boolean isInsertBefore() {
        return this.insertBefore;
    }

    public boolean isTargetingInsertion() {
        return this.targetsInsertion;
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
        for (ItemStack stack : this.stacks) {
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
}
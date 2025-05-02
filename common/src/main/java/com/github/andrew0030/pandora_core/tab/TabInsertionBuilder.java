package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A builder class for constructing and applying {@link TabInsertion} instances.
 *
 * @implNote When inserting multiple {@link ItemStack ItemStacks} that are all
 * next to each other, use a single {@link TabInsertion} instance for efficiency.
 */
public class TabInsertionBuilder {
    private final ResourceKey<CreativeModeTab> tab;
    private final List<ItemStack> stacks = new ArrayList<>();
    private TabVisibility visibility = TabVisibility.PARENT_AND_SEARCH_TABS;
    private ItemStack target;
    private boolean insertBefore;
    private boolean targetsInsertion;

    /**
     * Constructs a new builder for the given {@link CreativeModeTab}.
     *
     * @param tab The {@link ResourceKey} of the {@link CreativeModeTab} to insert objects into.
     */
    TabInsertionBuilder(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
    }

    /**
     * Adds one or more {@link ItemStack} objects to the insertion.<br/>
     * This method is useful if its necessary to add extra data (such as NBT) to an {@link ItemStack}.<br/>
     * If extra data isn't needed use {@link TabInsertionBuilder#add(ItemLike...)} instead.
     *
     * @param stack The {@link ItemStack} objects to add.
     * @return The current {@link TabInsertionBuilder} instance, for chaining method calls.
     */
    public TabInsertionBuilder add(ItemStack... stack) {
        this.stacks.addAll(Arrays.asList(stack));
        return this;
    }

    /**
     * Adds one or more {@link ItemLike} objects to the insertion.
     *
     * @param item The {@link ItemLike} objects to add.
     * @return The current {@link TabInsertionBuilder} instance, for chaining method calls.
     */
    public TabInsertionBuilder add(ItemLike... item) {
        this.stacks.addAll(Arrays.stream(item).map(ItemStack::new).toList());
        return this;
    }

    /**
     * Specifies that the inserted objects should be placed <strong>before</strong> the given target object.<br/>
     * This method is useful if its necessary to add extra data (such as NBT) to the {@link ItemStack}.<br/>
     * If extra data isn't needed use {@link TabInsertionBuilder#insertBefore(ItemLike)} instead.
     *
     * @param target The existing {@link ItemStack} to insert objects before.
     * @return The current {@link TabInsertionBuilder} instance, for chaining.
     */
    public TabInsertionBuilder insertBefore(ItemStack target) {
        this.target = target;
        this.insertBefore = true;
        return this;
    }

    /**
     * Specifies that the inserted objects should be placed <strong>before</strong> the given target object.
     *
     * @param target The existing {@link ItemLike} to insert objects before.
     * @return The current {@link TabInsertionBuilder} instance, for chaining.
     */
    public TabInsertionBuilder insertBefore(ItemLike target) {
        return this.insertBefore(new ItemStack(target));
    }

    /**
     * Specifies that the inserted objects should be placed <strong>after</strong> the given target object.<br/>
     * This method is useful if its necessary to add extra data (such as NBT) to the {@link ItemStack}.<br/>
     * If extra data isn't needed use {@link TabInsertionBuilder#insertAfter(ItemLike)} instead.
     *
     * @param target The existing {@link ItemStack} to insert objects after.
     * @return The current {@link TabInsertionBuilder} instance, for chaining.
     */
    public TabInsertionBuilder insertAfter(ItemStack target) {
        this.target = target;
        return this;
    }

    /**
     * Specifies that the inserted objects should be placed <strong>after</strong> the given target object.
     *
     * @param target The existing {@link ItemLike} to insert objects after.
     * @return The current {@link TabInsertionBuilder} instance, for chaining.
     */
    public TabInsertionBuilder insertAfter(ItemLike target) {
        return this.insertAfter(new ItemStack(target));
    }

    /**
     * Marks this {@link TabInsertion} as dependent on another {@link TabInsertion},
     * indicating that it must be included in the dependency sorting process.
     * <p>
     * <strong>When to use:</strong> Only call this if the insertion's target refers to an object
     * inserted by another {@link TabInsertion}. This is not needed for insertions that target
     * regular objects already present in the creative tab.
     * </p>
     *
     * @return The current {@link TabInsertionBuilder} instance, for chaining.
     */
    public TabInsertionBuilder targetsInsertion() {
        this.targetsInsertion = true;
        return this;
    }

    /**
     * Sets the visibility of the tab insertion.<br/>
     * This controls whether the insertion appears in the search tab, parent tab, or both.
     *
     * @param visibility The {@link TabVisibility} of the insertion.
     * @return The current {@link TabInsertionBuilder} instance, for chaining method calls.
     */
    public TabInsertionBuilder visibility(TabVisibility visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * Applies the configured {@link TabInsertion} to the tab's insertion list.
     */
    @SuppressWarnings("deprecation")
    public void apply() {
        TabInsertionManager.TAB_INSERTIONS
                .computeIfAbsent(this.tab, k -> new ArrayList<>())
                .add(new TabInsertion(this.stacks, this.visibility, this.target, this.insertBefore, this.targetsInsertion));
    }
}
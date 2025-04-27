package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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
    public void apply() {
        PaCoTabManager.TAB_INSERTIONS
                .computeIfAbsent(this.tab, k -> new ArrayList<>())
                .add(new TabInsertion(tab, this::insert, this.visibility, this.target, this.stacks));
    }

    /**
     * Inserts the {@link ItemStack ItemStacks} into the given list according to the builder's rules.
     * <p>
     * If a target item was specified and found, the stacks will be inserted either before or after (depending on configuration).
     * If no target is specified or found, the stacks are simply appended to the end of the list.
     * </p>
     *
     * @param list The list of {@link ItemStack ItemStacks} to modify.
     */
    private void insert(List<ItemStack> list) {
        // If there is no target we simply insert the stacks at the end of the tab
        if (this.target == null) {
            list.addAll(this.stacks);
            return;
        }
        // Inserts the stacks into the list if the list contains the target
        ListIterator<ItemStack> iterator = this.insertBefore
                ? list.listIterator(0)            // Start at beginning when inserting BEFORE
                : list.listIterator(list.size()); // Start at end when inserting AFTER
        while (this.insertBefore ? iterator.hasNext() : iterator.hasPrevious()) {
            ItemStack current = this.insertBefore ? iterator.next() : iterator.previous();
            boolean isMatching = this.target.hasTag() ? ItemStack.matches(current, this.target) : current.is(this.target.getItem());
            if (isMatching) {
                if (this.insertBefore) {
                    iterator.previous(); // Step back when inserting before
                } else {
                    iterator.next(); // Step forward when inserting after
                }
                this.stacks.forEach(iterator::add);
                return;
            }
        }
        // If the target wasn't in the list we insert the stacks at the end of the tab
        list.addAll(this.stacks);
    }
}
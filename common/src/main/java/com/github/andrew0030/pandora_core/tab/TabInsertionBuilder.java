package com.github.andrew0030.pandora_core.tab;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

//TODO write javadoc for this
public class TabInsertionBuilder {
    private final ResourceKey<CreativeModeTab> tab;
    private final List<ItemStack> stacks = new ArrayList<>();
    private TabVisibility visibility = TabVisibility.PARENT_AND_SEARCH_TABS;
    private ItemStack target;
    private boolean insertBefore;

    TabInsertionBuilder(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
    }

    public TabInsertionBuilder add(ItemStack... stack) {
        this.stacks.addAll(Arrays.asList(stack));
        return this;
    }

    public TabInsertionBuilder add(Item... item) {
        this.stacks.addAll(Arrays.stream(item).map(ItemStack::new).toList());
        return this;
    }

    public TabInsertionBuilder visibility(TabVisibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public TabInsertionBuilder insertBefore(ItemStack target) {
        this.target = target;
        this.insertBefore = true;
        return this;
    }

    public TabInsertionBuilder insertBefore(Item target) {
        return this.insertBefore(new ItemStack(target));
    }

    public TabInsertionBuilder insertAfter(ItemStack target) {
        this.target = target;
        return this;
    }

    public TabInsertionBuilder insertAfter(Item target) {
        return this.insertAfter(new ItemStack(target));
    }

    public void apply() {
        PaCoTabManager.TAB_INSERTIONS.add(new TabInsertion(tab, this::insert, this.visibility));
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

        ListIterator<ItemStack> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ItemStack current = iterator.next();
            boolean isMatching = target.hasTag() ? ItemStack.matches(current, target) : current.is(target.getItem());
            if (isMatching) {
                if (this.insertBefore)
                    iterator.previous(); // Step back to insert before
                this.stacks.forEach(iterator::add);
                return;
            }
        }
        // TODO: expand logic to maybe also scan pending modifications for the target and then adjust order?
        // list.addAll(this.stacks);
    }
}
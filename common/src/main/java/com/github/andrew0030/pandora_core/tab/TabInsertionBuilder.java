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
    private Item target;
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

    public TabInsertionBuilder insertBefore(Item target) {
        this.target = target;
        this.insertBefore = true;
        return this;
    }

    public TabInsertionBuilder insertAfter(Item target) {
        this.target = target;
        return this;
    }

    public void apply() {
        PaCoTabManager.TAB_INSERTIONS.add(new TabInsertion(tab, this::insert, this.visibility));
    }

    private void insert(List<ItemStack> list) {
        if (this.target == null) {
            list.addAll(this.stacks);
            return;
        }

        ListIterator<ItemStack> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ItemStack current = iterator.next();
            if (current.is(this.target)) {
                if (this.insertBefore)
                    iterator.previous(); // Step back to insert before
                this.stacks.forEach(iterator::add);
                return;
            }
        }
        // TODO: expand logic to maybe also scan pending modifications for the target and then adjust order?
    }
}
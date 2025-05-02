package com.github.andrew0030.pandora_core.tab;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

record ItemKey(Item item, CompoundTag tag) {
    static ItemKey of(ItemStack stack) {
        return new ItemKey(stack.getItem(), stack.hasTag() ? stack.getTag() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemKey other)) return false;
        if (this.item != other.item) return false;
        if (this.tag == null || other.tag == null) return true;
        return Objects.equals(this.tag, other.tag);
    }

    @Override
    public int hashCode() {
        return tag == null ? item.hashCode() : Objects.hash(item, tag);
    }
}
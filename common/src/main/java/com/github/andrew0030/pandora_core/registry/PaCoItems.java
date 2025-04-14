package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

//TODO remove this later when done with testing.
public class PaCoItems {
    public static final PaCoRegistry<Item> ITEMS = new PaCoRegistry<>(BuiltInRegistries.ITEM, PandoraCore.MOD_ID);

    public static final Supplier<Item> TEST = ITEMS.add("test", () -> new BoneMealItem(new Item.Properties()));
    public static final Supplier<Item> FUNK = ITEMS.add("funk", () -> new BoneMealItem(new Item.Properties()));
}
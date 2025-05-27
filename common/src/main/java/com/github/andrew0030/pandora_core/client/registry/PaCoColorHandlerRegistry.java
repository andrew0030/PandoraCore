package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// TODO write javadocs
public class PaCoColorHandlerRegistry {
    private final Map<Supplier<Block>, BlockColor> blockColors = new HashMap<>();
    private final Map<Supplier<? extends ItemLike>, ItemColor> itemColors = new HashMap<>();

    @SafeVarargs
    public final void addBlock(BlockColor blockColor, Supplier<Block>... blocks) {
        for (Supplier<Block> block : blocks)
            this.blockColors.put(block, blockColor);
    }

    @SafeVarargs
    public final void addItem(ItemColor itemColor, Supplier<? extends ItemLike>... items) {
        for (Supplier<? extends ItemLike> item : items)
            this.itemColors.put(item, itemColor);
    }

    public void register() {
        Services.REGISTRY.registerColorHandlers(this.blockColors, this.itemColors);
    }
}
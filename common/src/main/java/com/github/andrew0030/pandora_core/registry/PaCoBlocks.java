package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.blocks.TestBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class PaCoBlocks {
    public static final PaCoRegistry<Block> BLOCKS = new PaCoRegistry<>(BuiltInRegistries.BLOCK, PandoraCore.MOD_ID);

    public static final Supplier<Block> TEST = PaCoBlocks.createBlock("test", TestBlock::new);

    private static Supplier<Block> createBlock(String name, Supplier<Block> supplier) {
        Supplier<Block> block = BLOCKS.add(name, supplier);
        PaCoItems.ITEMS.add(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
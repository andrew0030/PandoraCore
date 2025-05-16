package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.blocks.InstancingTestBlock;
import com.github.andrew0030.pandora_core.blocks.TestBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class PaCoBlocks {
    public static final PaCoRegistry<Block> BLOCKS = new PaCoRegistry<>(BuiltInRegistries.BLOCK, PandoraCore.MOD_ID);

    public static final Supplier<Block> TEST            = PaCoBlocks.createBlock("test", TestBlock::new);
    public static final Supplier<Block> INSTANCING_TEST = PaCoBlocks.createBlock("instancing_test", () -> new InstancingTestBlock(BlockBehaviour.Properties.copy(Blocks.STONE).forceSolidOff().noOcclusion().noCollission()));
    public static final Supplier<Block> CONNECTED_BLOCK = PaCoBlocks.createBlock("connected_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()));

    private static Supplier<Block> createBlock(String name, Supplier<Block> supplier) {
        Supplier<Block> block = BLOCKS.add(name, supplier);
        PaCoItems.ITEMS.add(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
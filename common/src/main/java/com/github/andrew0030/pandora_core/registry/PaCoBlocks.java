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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class PaCoBlocks {
    public static final PaCoRegistry<Block> BLOCKS = new PaCoRegistry<>(BuiltInRegistries.BLOCK, PandoraCore.MOD_ID);

    public static final Supplier<Block> TEST            = PaCoBlocks.createBlock("test", TestBlock::new);
    public static final Supplier<Block> INSTANCING_TEST = PaCoBlocks.createBlock("instancing_test", () -> new InstancingTestBlock(BlockBehaviour.Properties.copy(Blocks.STONE).forceSolidOff().noOcclusion().noCollission()));
    public static final Supplier<Block> CONNECTED_BLOCK = PaCoBlocks.createBlock("connected_block", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> CTM_HORIZONTAL  = PaCoBlocks.createBlock("ctm_horizontal", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> CTM_VERTICAL    = PaCoBlocks.createBlock("ctm_vertical", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> CTM_RANDOM      = PaCoBlocks.createBlock("ctm_random", () -> new Block(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> CTM_REPEAT      = PaCoBlocks.createBlock("ctm_repeat", () -> new Block(BlockBehaviour.Properties.of()));
    public static final Supplier<Block> FOLIAGE_TEST    = PaCoBlocks.createBlock("foliage_test", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).noOcclusion().isViewBlocking((state, blockGetter, pos) -> false).pushReaction(PushReaction.DESTROY)));

    private static Supplier<Block> createBlock(String name, Supplier<Block> supplier) {
        Supplier<Block> block = BLOCKS.add(name, supplier);
        PaCoItems.ITEMS.add(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
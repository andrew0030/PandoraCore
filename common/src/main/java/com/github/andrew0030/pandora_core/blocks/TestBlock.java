package com.github.andrew0030.pandora_core.blocks;

import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends BaseEntityBlock {

    public TestBlock() {
        super(TestBlock.getProperties());
    }

    private static Properties getProperties() {
        Properties properties = Block.Properties.of().mapColor(MapColor.WOOL);
        properties.strength(1.5F, 6.0F);
        properties.noOcclusion();
        return properties;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }
}
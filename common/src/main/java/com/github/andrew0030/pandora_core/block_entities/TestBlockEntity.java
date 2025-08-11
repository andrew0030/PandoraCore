package com.github.andrew0030.pandora_core.block_entities;

import com.github.andrew0030.pandora_core.registry.test.PaCoBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlockEntity extends BlockEntity {

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(PaCoBlockEntities.TEST.get(), pos, state);
    }
}
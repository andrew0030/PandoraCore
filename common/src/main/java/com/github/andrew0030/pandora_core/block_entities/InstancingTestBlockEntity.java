package com.github.andrew0030.pandora_core.block_entities;

import com.github.andrew0030.pandora_core.registry.PaCoBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InstancingTestBlockEntity extends BlockEntity {

    public InstancingTestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public InstancingTestBlockEntity(BlockPos pos, BlockState state) {
        super(PaCoBlockEntities.INSTANCING_TEST.get(), pos, state);
    }
}
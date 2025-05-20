package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RandomCTMType extends BaseCTMType {
    private int columns = 1;
    private int rows = 1;

    @Override
    public int getColumns() {
        return this.columns;
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    public void setDimensions(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return false;
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        if (pos == null) return 0;
        rand.setSeed(state.getSeed(pos));
        return rand.nextInt(this.columns * this.rows);
    }
}
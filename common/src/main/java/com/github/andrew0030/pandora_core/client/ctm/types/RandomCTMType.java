package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RandomCTMType extends BaseCTMType {
    private int columns = 1;
    private int rows = 1;
    private int tiles = 0;

    @Override
    public int getColumns() {
        return this.columns;
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public boolean requiresAxisAligned() {
        return false;
    }

    @Override
    public boolean requiresDiagonal() {
        return false;
    }

    public void setDimensions(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public void setTiles(int tiles) {
        this.tiles = tiles;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return false;
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        if (pos == null) return 0;
        rand.setSeed(state.getSeed(pos));
        return rand.nextInt(this.totalTiles());
    }

    @Override
    public int totalTiles() {
        if (this.tiles != 0)
            return this.tiles;
        return this.columns * this.rows;
    }
}
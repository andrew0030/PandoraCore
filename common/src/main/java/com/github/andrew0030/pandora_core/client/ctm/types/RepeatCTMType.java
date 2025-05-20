package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RepeatCTMType extends BaseCTMType {
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
        if (pos == null || side == null) return 0;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return switch (side) {
            case UP    -> calculateTile(x, z, false, false);
            case DOWN  -> calculateTile(x, z + 1, false, true);
            case NORTH -> calculateTile(x + 1, y + 1, true, true);
            case SOUTH -> calculateTile(x, y + 1, false, true);
            case WEST  -> calculateTile(z, y + 1, false, true);
            case EAST  -> calculateTile(z + 1, y + 1, true, true);
        };
    }

    private int calculateTile(int a, int b, boolean flipA, boolean flipB) {
        if (flipA) a = -a;
        if (flipB) b = -b;

        int col = Math.floorMod(a, columns);
        int row = Math.floorMod(b, rows);
        return row * columns + col;
    }
}
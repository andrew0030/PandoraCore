package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.EnumSet;

public class HorizontalCTMType extends BaseCTMType {
    private static final EnumSet<FaceAdjacency> RELEVANT_ADJ = EnumSet.of(FaceAdjacency.LEFT, FaceAdjacency.RIGHT);
    private static final int[] CTM_LOOKUP = new int[25];

    static {
        Arrays.fill(CTM_LOOKUP, 3); // Defaults to index 0
        // Isolated
        CTM_LOOKUP[0] = 3;      // No connections
        // Horizontal
        CTM_LOOKUP[16] = 0;     // Right
        CTM_LOOKUP[24] = 1;     // Left + Right
        CTM_LOOKUP[8] = 2;      // Left
    }

    @Override
    public int getColumns() {
        return 4;
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public boolean requiresAxisAligned() {
        return true;
    }

    @Override
    public boolean requiresDiagonal() {
        return false;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return RELEVANT_ADJ.contains(adjacency);
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        return CTM_LOOKUP[Math.floorMod(bit, CTM_LOOKUP.length)];
    }
}
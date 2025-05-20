package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Map;

public class VerticalCTMType extends BaseCTMType {
    private static final EnumSet<FaceAdjacency> RELEVANT_ADJ = EnumSet.of(FaceAdjacency.TOP, FaceAdjacency.BOTTOM);
    private static final Map<Integer, Integer> CTM_LOOKUP = Map.ofEntries(
            Map.entry(0, 3),     // No connections (isolated)
            // Vertical
            Map.entry(64, 2),    // Bottom
            Map.entry(66, 1),    // Top + Bottom
            Map.entry(2, 0)      // Top
    );

    @Override
    public int getColumns() {
        return 4;
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return RELEVANT_ADJ.contains(adjacency);
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        return CTM_LOOKUP.getOrDefault(bit, 3);
    }
}
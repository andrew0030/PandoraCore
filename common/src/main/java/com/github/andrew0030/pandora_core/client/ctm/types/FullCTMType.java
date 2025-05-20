package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class FullCTMType extends BaseCTMType {
    private static final Map<Integer, Integer> CTM_LOOKUP = Map.ofEntries(
            Map.entry(0, 0),     // No connections (isolated)
            // Horizontal
            Map.entry(16, 1),    // Right
            Map.entry(24, 2),    // Left + Right
            Map.entry(8, 3),     // Left
            // Vertical
            Map.entry(64, 12),   // Bottom
            Map.entry(66, 24),   // Top + Bottom
            Map.entry(2, 36),    // Top
            // Two sides one diagonal
            Map.entry(208, 13),  // Right + Bottom + Bottom-right
            Map.entry(104, 15),  // Left + Bottom + Bottom-left
            Map.entry(22, 37),   // Right + Top + Top-right
            Map.entry(11, 39),   // Left + Top + Top-left
            // Two sides no diagonals
            Map.entry(80, 4),    // Right + Bottom
            Map.entry(72, 5),    // Left + Bottom
            Map.entry(18, 16),   // Right + Top
            Map.entry(10, 17),   // Left + Top
            // Three sides no diagonals
            Map.entry(26, 18),   // Left + Top + Right
            Map.entry(88, 7),    // Left + Bottom + Right
            Map.entry(74, 19),   // Top + Left + Bottom
            Map.entry(82, 6),    // Top + Right + Bottom
            // Three sides one diagonal
            Map.entry(27, 42),   // Left + Top + Right + Top-left
            Map.entry(30, 40),   // Left + Top + Right + Top-right
            Map.entry(120, 29),  // Left + Bottom + Right + Bottom-left
            Map.entry(216, 31),  // Left + Bottom + Right + Bottom-right
            Map.entry(75, 41),   // Top + Left + Bottom + Top-left
            Map.entry(106, 43),  // Top + Left + Bottom + Bottom-left
            Map.entry(86, 30),   // Top + Right + Bottom + Top-right
            Map.entry(210, 28),  // Top + Right + Bottom + Bottom-right
            // Three sides two diagonals
            Map.entry(31, 38),   // Left + Top + Right + Top-left + Top-right
            Map.entry(248, 14),  // Left + Bottom + Right + Bottom-left + Bottom-right
            Map.entry(107, 27),  // Top + Left + Bottom + Top-left + Bottom-left
            Map.entry(214, 25),  // Top + Right + Bottom + Top-right + Bottom-right
            // Four sides one diagonal
            Map.entry(91, 20),   // Cross + Top-left
            Map.entry(94, 8),    // Cross + Top-right
            Map.entry(122, 21),  // Cross + Bottom-left
            Map.entry(218, 9),   // Cross + Bottom-right
            // Four sides two diagonals touching
            Map.entry(95, 11),   // Cross + Top-left + Top-right
            Map.entry(222, 23),  // Cross + Top-right + Bottom-right
            Map.entry(250, 22),  // Cross + Bottom-left + Bottom-right
            Map.entry(123, 10),  // Cross + Top-left + Bottom-left
            // Four sides two diagonals across
            Map.entry(126, 34),   // Cross + Top-right + Bottom-left
            Map.entry(219, 35),   // Cross + Top-left + Bottom-right
            // Four sides three diagonals
            Map.entry(254, 45),   // All except Top-left
            Map.entry(251, 44),   // All except Top-right
            Map.entry(127, 32),   // All except Bottom-right
            Map.entry(223, 33),   // All except Bottom-left
            // Four sides four diagonals
            Map.entry(255, 26),   // All neighbors
            // Four sides no diagonals
            Map.entry(90, 46)     // Cross
    );

    @Override
    public int getColumns() {
        return 12;
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return true;
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        return CTM_LOOKUP.getOrDefault(bit, 0);
    }
}
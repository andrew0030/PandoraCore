package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class FullCTMType extends BaseCTMType {
    private static final int[] CTM_LOOKUP = new int[256];

    static {
        Arrays.fill(CTM_LOOKUP, 0); // Defaults to index 0
        // Isolated
        CTM_LOOKUP[0] = 0;      // No connections
        // Horizontal
        CTM_LOOKUP[16] = 1;     // Right
        CTM_LOOKUP[24] = 2;     // Left + Right
        CTM_LOOKUP[8] = 3;      // Left
        // Vertical
        CTM_LOOKUP[64] = 12;    // Bottom
        CTM_LOOKUP[66] = 24;    // Top + Bottom
        CTM_LOOKUP[2] = 36;     // Top
        // Two sides one diagonal
        CTM_LOOKUP[208] = 13;   // Right + Bottom + Bottom-right
        CTM_LOOKUP[104] = 15;   // Left + Bottom + Bottom-left
        CTM_LOOKUP[22] = 37;    // Right + Top + Top-right
        CTM_LOOKUP[11] = 39;    // Left + Top + Top-left
        // Two sides no diagonals
        CTM_LOOKUP[80] = 4;     // Right + Bottom
        CTM_LOOKUP[72] = 5;     // Left + Bottom
        CTM_LOOKUP[18] = 16;    // Right + Top
        CTM_LOOKUP[10] = 17;    // Left + Top
        // Three sides no diagonals
        CTM_LOOKUP[26] = 18;    // Left + Top + Right
        CTM_LOOKUP[88] = 7;     // Left + Bottom + Right
        CTM_LOOKUP[74] = 19;    // Top + Left + Bottom
        CTM_LOOKUP[82] = 6;     // Top + Right + Bottom
        // Three sides one diagonal
        CTM_LOOKUP[27] = 42;    // Left + Top + Right + Top-left
        CTM_LOOKUP[30] = 40;    // Left + Top + Right + Top-right
        CTM_LOOKUP[120] = 29;   // Left + Bottom + Right + Bottom-left
        CTM_LOOKUP[216] = 31;   // Left + Bottom + Right + Bottom-right
        CTM_LOOKUP[75] = 41;    // Top + Left + Bottom + Top-left
        CTM_LOOKUP[106] = 43;   // Top + Left + Bottom + Bottom-left
        CTM_LOOKUP[86] = 30;    // Top + Right + Bottom + Top-right
        CTM_LOOKUP[210] = 28;   // Top + Right + Bottom + Bottom-right
        // Three sides two diagonals
        CTM_LOOKUP[31] = 38;    // Left + Top + Right + Top-left + Top-right
        CTM_LOOKUP[248] = 14;   // Left + Bottom + Right + Bottom-left + Bottom-right
        CTM_LOOKUP[107] = 27;   // Top + Left + Bottom + Top-left + Bottom-left
        CTM_LOOKUP[214] = 25;   // Top + Right + Bottom + Top-right + Bottom-right
        // Four sides one diagonal
        CTM_LOOKUP[91] = 20;    // Cross + Top-left
        CTM_LOOKUP[94] = 8;     // Cross + Top-right
        CTM_LOOKUP[122] = 21;   // Cross + Bottom-left
        CTM_LOOKUP[218] = 9;    // Cross + Bottom-right
        // Four sides two diagonals touching
        CTM_LOOKUP[95] = 11;    // Cross + Top-left + Top-right
        CTM_LOOKUP[222] = 23;   // Cross + Top-right + Bottom-right
        CTM_LOOKUP[250] = 22;   // Cross + Bottom-left + Bottom-right
        CTM_LOOKUP[123] = 10;   // Cross + Top-left + Bottom-left
        // Four sides two diagonals across
        CTM_LOOKUP[126] = 34;    // Cross + Top-right + Bottom-left
        CTM_LOOKUP[219] = 35;    // Cross + Top-left + Bottom-right
        // Four sides three diagonals
        CTM_LOOKUP[254] = 45;    // All except Top-left
        CTM_LOOKUP[251] = 44;    // All except Top-right
        CTM_LOOKUP[127] = 32;    // All except Bottom-right
        CTM_LOOKUP[223] = 33;    // All except Bottom-left
        // Four sides four diagonals
        CTM_LOOKUP[255] = 26;    // All neighbors
        // Four sides no diagonals
        CTM_LOOKUP[90] = 46;     // Cross
    }

    @Override
    public int getColumns() {
        return 12;
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public boolean requiresAxisAligned() {
        return true;
    }

    @Override
    public boolean requiresDiagonal() {
        return true;
    }

    @Override
    public boolean isRelevantAdjacency(FaceAdjacency adjacency) {
        return true;
    }

    @Override
    public int getTileIndex(int bit, BlockState state, BlockPos pos, Direction side, RandomSource rand) {
        if (bit > 255 || bit < 0) return 47;
        return CTM_LOOKUP[Math.floorMod(bit, CTM_LOOKUP.length)];
    }

    @Override
    public int totalTiles() {
        return 47;
    }
}
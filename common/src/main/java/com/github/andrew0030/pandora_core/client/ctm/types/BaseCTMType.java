package com.github.andrew0030.pandora_core.client.ctm.types;

import com.github.andrew0030.pandora_core.client.ctm.FaceAdjacency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class BaseCTMType {

    /**
     * @return How many columns (width) the sheet version of the texture has.
     * @implNote The value needs to be bigger than 0
     */
    public abstract int getColumns();

    /**
     * @return How many rows (height) the sheet version of the texture has.
     * @implNote The value needs to be bigger than 0
     */
    public abstract int getRows();

    /** @return Whether to check axis-aligned connections for this type. */
    public abstract boolean requiresAxisAligned();

    /** @return Whether to check diagonal connections for this type. */
    public abstract boolean requiresDiagonal();

    public abstract boolean isRelevantAdjacency(FaceAdjacency adjacency);

    public abstract int getTileIndex(int bit, BlockState state, @Nullable BlockPos pos, Direction side, RandomSource rand);
}
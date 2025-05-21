package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.client.ctm.types.BaseCTMType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class BaseCTMModel implements BakedModel {
    private static final Direction[] ALL_DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    private static final EnumMap<Direction, EnumSet<FaceAdjacency>> EMPTY_MAP = new EnumMap<>(Direction.class);
    protected final BakedModel model;
    protected final CTMSpriteResolver spriteResolver;
    protected final CTMDataResolver dataResolver;

    static {
        for (Direction direction : ALL_DIRECTIONS)
            EMPTY_MAP.put(direction, EnumSet.noneOf(FaceAdjacency.class));
    }

    public BaseCTMModel(BakedModel model, CTMSpriteResolver spriteResolver, CTMDataResolver dataResolver) {
        this.model = model;
        this.spriteResolver = spriteResolver;
        this.dataResolver = dataResolver;
    }

    /**
     * Computes a map of face directions and their corresponding connected adjacent positions.
     * This method inspects all neighboring blocks around each face of the current block and
     * determines which {@link FaceAdjacency} values are connected and visible for CTM rendering.
     * <p>
     * A direction is considered "connected" if:
     * <ul>
     *     <li>The adjacent block in that direction is of the same type as the current block</li>
     *     <li>The adjacent block is not occluded by another block of the same type directly in front of the face</li>
     *     <li>For diagonal connections, the adjacent axis-aligned neighbors must also be present (to avoid gaps)</li>
     * </ul>
     *
     * @param level     The {@link BlockAndTintGetter} (level) in which the block resides
     * @param pos       The position of the current block
     * @param state     The block state of the current block
     * @return An {@link EnumMap} of {@link Direction Directions} and all their relevant {@link FaceAdjacency} values.
     */
    public EnumMap<Direction, EnumSet<FaceAdjacency>> computeFaceConnections(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        // TODO: Add a config option to disable CTM, and an option to disable "in front of" checks

        // If the CTM type isn't valid we don't perform any logic.
        if (this.dataResolver.getCTMType() == null) return null;
        BaseCTMType type = this.dataResolver.getCTMType();

        // If there is no axis-aligned checks we return early
        if (!type.requiresAxisAligned()) return EMPTY_MAP;

        // A map of directions and all their relevant adjacent blocks
        EnumMap<Direction, EnumSet<FaceAdjacency>> faceConnections = new EnumMap<>(Direction.class);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean checkInFront = true; // TODO add a dataResolver entry and config option

        //   North Y: 1       North Y: 0         North Y: -1
        //  ____________    _______________    _______________
        // | 0 | 1 | 2 |   | 9  | 10 | 11 |   | 17 | 18 | 19 |
        // | 3 | 4 | 5 |   | 12 |    | 13 |   | 20 | 21 | 22 |  East
        // | 6 | 7 | 8 |   | 14 | 15 | 16 |   | 23 | 24 | 25 |
        // ------------    ---------------    ---------------
        int bits = 0;
        // Performs all required block checks and stores them in "bits" using bitwise operations
        if (type.requiresAxisAligned()) {
            // The standard 3D cross required for all basic CTM that check neighbors
            mutablePos.set(pos).move(Direction.DOWN);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 21);
            mutablePos.set(pos).move(Direction.UP);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 4);
            mutablePos.set(pos).move(Direction.NORTH);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 10);
            mutablePos.set(pos).move(Direction.SOUTH);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 15);
            mutablePos.set(pos).move(Direction.WEST);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 12);
            mutablePos.set(pos).move(Direction.EAST);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 13);

            if (type.requiresDiagonal() || checkInFront) {
                // If we need to check diagonals or "in front of" we need the blocks next to the cross
                // Top Layer
                mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 1);
                mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 7);
                mutablePos.set(pos).move(Direction.UP).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 3);
                mutablePos.set(pos).move(Direction.UP).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 5);
                // Middle Layer
                mutablePos.set(pos).move(Direction.NORTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 9);
                mutablePos.set(pos).move(Direction.NORTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 11);
                mutablePos.set(pos).move(Direction.SOUTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 14);
                mutablePos.set(pos).move(Direction.SOUTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 16);
                // Bottom Layer
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 18);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 24);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 20);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 22);
            }

            if (type.requiresDiagonal() && checkInFront) {
                // If we need to check "in front of" diagonals we require the outermost edge blocks
                // Top Layer
                mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 0);
                mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 2);
                mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 6);
                mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 8);
                // Bottom Layer
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 17);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 19);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH).move(Direction.WEST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 23);
                mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH).move(Direction.EAST);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) bits = this.setBit(bits, 25);
            }
        }

        // After all block checks have been done we populate the map
        for (Direction direction : ALL_DIRECTIONS) {
            EnumSet<FaceAdjacency> set = EnumSet.noneOf(FaceAdjacency.class);

            switch (direction) {
                case DOWN:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 21)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 15) && (!checkInFront || !this.isBitSet(bits, 24)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 10) && (!checkInFront || !this.isBitSet(bits, 18)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 12) && (!checkInFront || !this.isBitSet(bits, 20)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 13) && (!checkInFront || !this.isBitSet(bits, 22)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 14) && !this.isBitSet(bits, 23)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 16) && !this.isBitSet(bits, 25)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 9) && !this.isBitSet(bits, 17)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 11) && !this.isBitSet(bits, 19))
                                set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
                case UP:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 4)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 10) && (!checkInFront || !this.isBitSet(bits, 1)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 15) && (!checkInFront || !this.isBitSet(bits, 7)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 12) && (!checkInFront || !this.isBitSet(bits, 3)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 13) && (!checkInFront || !this.isBitSet(bits, 5)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 9) && !this.isBitSet(bits, 0)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 11) && !this.isBitSet(bits, 2)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 14) && !this.isBitSet(bits, 6)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 16) && !this.isBitSet(bits, 8)) set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
                case NORTH:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 10)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 4) && (!checkInFront || !this.isBitSet(bits, 1)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 21) && (!checkInFront || !this.isBitSet(bits, 18)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 13) && (!checkInFront || !this.isBitSet(bits, 11)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 12) && (!checkInFront || !this.isBitSet(bits, 9)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 5) && !this.isBitSet(bits, 2)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 3) && !this.isBitSet(bits, 0)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 22) && !this.isBitSet(bits, 19)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 20) && !this.isBitSet(bits, 17))
                                set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
                case SOUTH:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 15)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 4) && (!checkInFront || !this.isBitSet(bits, 7)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 21) && (!checkInFront || !this.isBitSet(bits, 24)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 12) && (!checkInFront || !this.isBitSet(bits, 14)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 13) && (!checkInFront || !this.isBitSet(bits, 16)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 3) && !this.isBitSet(bits, 6)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 5) && !this.isBitSet(bits, 8)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 20) && !this.isBitSet(bits, 23)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 22) && !this.isBitSet(bits, 25))
                                set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
                case WEST:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 12)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 4) && (!checkInFront || !this.isBitSet(bits, 3)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 21) && (!checkInFront || !this.isBitSet(bits, 20)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 10) && (!checkInFront || !this.isBitSet(bits, 9)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 15) && (!checkInFront || !this.isBitSet(bits, 14)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 1) && !this.isBitSet(bits, 0)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 7) && !this.isBitSet(bits, 6)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 18) && !this.isBitSet(bits, 17)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 24) && !this.isBitSet(bits, 23))
                                set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
                case EAST:
                    // If the face is covered and "in front of" checks are active we skip further logic
                    if (checkInFront && this.isBitSet(bits, 13)) {
                        faceConnections.put(direction, set);
                        continue;
                    }
                    // Axis-aligned
                    if (this.isBitSet(bits, 4) && (!checkInFront || !this.isBitSet(bits, 5)))
                        set.add(FaceAdjacency.TOP);
                    if (this.isBitSet(bits, 21) && (!checkInFront || !this.isBitSet(bits, 22)))
                        set.add(FaceAdjacency.BOTTOM);
                    if (this.isBitSet(bits, 15) && (!checkInFront || !this.isBitSet(bits, 16)))
                        set.add(FaceAdjacency.LEFT);
                    if (this.isBitSet(bits, 10) && (!checkInFront || !this.isBitSet(bits, 11)))
                        set.add(FaceAdjacency.RIGHT);
                    // Diagonals
                    if (type.requiresDiagonal()) {
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_LEFT)))
                            if (this.isBitSet(bits, 7) && !this.isBitSet(bits, 8)) set.add(FaceAdjacency.TOP_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.TOP_RIGHT)))
                            if (this.isBitSet(bits, 1) && !this.isBitSet(bits, 2)) set.add(FaceAdjacency.TOP_RIGHT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_LEFT)))
                            if (this.isBitSet(bits, 24) && !this.isBitSet(bits, 25)) set.add(FaceAdjacency.BOTTOM_LEFT);
                        if (set.containsAll(FaceAdjacency.getDiagonalDependencies(FaceAdjacency.BOTTOM_RIGHT)))
                            if (this.isBitSet(bits, 18) && !this.isBitSet(bits, 19))
                                set.add(FaceAdjacency.BOTTOM_RIGHT);
                    }
                    break;
            }

            faceConnections.put(direction, set);
        }

        // If there are any mutations for this variant we apply them.
        if (this.dataResolver.hasMutations()) {
            EnumMap<Direction, FaceAdjacency.Mutation> mutations = this.dataResolver.getMutations();
            EnumSet<FaceAdjacency> tempSet = EnumSet.noneOf(FaceAdjacency.class);
            for (Map.Entry<Direction, FaceAdjacency.Mutation> entry : mutations.entrySet()) {
                Direction direction = entry.getKey();
                FaceAdjacency.Mutation mutation = entry.getValue();
                EnumSet<FaceAdjacency> adjSet = faceConnections.get(direction);
                tempSet.clear();
                for (FaceAdjacency adj : adjSet)
                    tempSet.add(adj.transform(mutation));
                adjSet.clear();
                adjSet.addAll(tempSet);
            }
        }

        return faceConnections;
    }

    private boolean isBitSet(int bits, int index) {
        return (bits & (1 << index)) != 0;
    }

    private int setBit(int bits, int index) {
        return bits | (1 << index);
    }

    /**
     * Remaps a texture coordinate from one range to another.
     * <p>
     * This can be used for either U or V coordinates in UV mapping.
     *
     * @param value     The original coordinate to remap.
     * @param oldMin    The lower bound of the original coordinate range.
     * @param oldMax    The upper bound of the original coordinate range.
     * @param newMin    The lower bound of the target coordinate range.
     * @param newMax    The upper bound of the target coordinate range.
     * @return The remapped coordinate within the target range.
     */
    public float remapUV(float value, float oldMin, float oldMax, float newMin, float newMax) {
        float normalized = (value - oldMin) / (oldMax - oldMin);
        return newMin + normalized * (newMax - newMin);
    }

    // Delegates all BakedModel methods to the model
    @Override public boolean useAmbientOcclusion() { return this.model.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return this.model.isGui3d(); }
    @Override public boolean usesBlockLight() { return this.model.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return this.model.isCustomRenderer(); }
    @Override public @NotNull TextureAtlasSprite getParticleIcon() { return this.model.getParticleIcon(); }
    @Override public @NotNull ItemTransforms getTransforms() { return this.model.getTransforms(); }
    @Override public @NotNull ItemOverrides getOverrides() { return this.model.getOverrides(); }
}
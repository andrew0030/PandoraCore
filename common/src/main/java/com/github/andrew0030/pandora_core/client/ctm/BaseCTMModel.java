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

import java.util.EnumMap;
import java.util.EnumSet;

public abstract class BaseCTMModel implements BakedModel {
    private static final Direction[] ALL_DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    protected final BakedModel model;
    protected final CTMSpriteResolver spriteResolver;
    protected final CTMDataResolver dataResolver;

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
     * @return An {@link EnumMap} of {@link Direction Directions} and all their relevant {@link FaceAdjacency} values
     */
    public EnumMap<Direction, EnumSet<FaceAdjacency>> computeFaceConnections(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        // TODO: Add a config option to disable CTM, and an option to disable "in front of" checks


        // If the CTM type isn't valid we don't perform any logic.
        if (this.dataResolver.getCTMType() == null) return null;
        BaseCTMType type = this.dataResolver.getCTMType();

        // A map of directions and all their relevant adjacent blocks
        EnumMap<Direction, EnumSet<FaceAdjacency>> faceConnections = new EnumMap<>(Direction.class);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (Direction faceDirection : ALL_DIRECTIONS) {
            EnumSet<FaceAdjacency> set = EnumSet.noneOf(FaceAdjacency.class);

            // If there is a block in front of the face we skip
            mutablePos.set(pos).move(faceDirection);
            if (this.dataResolver.canConnectWith(level, mutablePos, state)) {
                faceConnections.put(faceDirection, set);
                continue;
            }

            // We check all axis-aligned blocks
            for (FaceAdjacency adj : FaceAdjacency.axisAlignedValues()) {
                BlockPos offset = FaceAdjacency.getOffset(faceDirection, adj);
                // Checks if there is a block adjacent
                mutablePos.set(pos).move(offset);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) {
                    // Ensures the block isn't covered by another
                    mutablePos.move(faceDirection);
                    if (!this.dataResolver.canConnectWith(level, mutablePos, state)) {
                        set.add(adj);
                    }
                }
            }

            // We check all diagonal blocks
            for (FaceAdjacency adj : FaceAdjacency.diagonalValues()) {
                // If the axis-aligned blocks next to the diagonal block are missing we skip
                if (!set.containsAll(FaceAdjacency.getDiagonalDependencies(adj))) continue;

                BlockPos offset = FaceAdjacency.getOffset(faceDirection, adj);
                // Checks if there is a block adjacent (diagonally)
                mutablePos.set(pos).move(offset);
                if (this.dataResolver.canConnectWith(level, mutablePos, state)) {
                    // Ensures the block isn't covered by another
                    mutablePos.move(faceDirection);
                    if (!this.dataResolver.canConnectWith(level, mutablePos, state)) {
                        set.add(adj);
                    }
                }
            }

            // Lastly we store each face direction and its valid adjacent blocks
            faceConnections.put(faceDirection, set);
        }

        // If there are any mutations for this variant we apply them.
        if (this.dataResolver.hasMutations()) {
            EnumMap<Direction, FaceAdjacency.Mutation> mutations = this.dataResolver.getMutations();
            EnumSet<FaceAdjacency> tempSet = EnumSet.noneOf(FaceAdjacency.class);
            for (Direction direction : mutations.keySet()) {
                FaceAdjacency.Mutation mutation = mutations.get(direction);
                EnumSet<FaceAdjacency> adjSet = faceConnections.get(direction);
                tempSet.clear();
                for (FaceAdjacency adj : adjSet)
                    tempSet.add(adj.transform(mutation));
                adjSet.clear();
                adjSet.addAll(tempSet);
                faceConnections.put(direction, adjSet);
            }
        }

        return faceConnections;
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
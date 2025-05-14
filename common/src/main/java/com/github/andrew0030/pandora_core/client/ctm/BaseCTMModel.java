package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.utils.collection.EnumMapUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public abstract class BaseCTMModel implements BakedModel {
    private static final Direction[] ALL_DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    protected final BakedModel model;

    //TODO eventually this should be determined/provided by the CTM type
    protected static final Map<Integer, Integer> CTM_LOOKUP = Map.ofEntries(
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

    public BaseCTMModel(BakedModel model) {
        this.model = model;
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
     * @param selfBlock The block type of the current block
     * @return A map of {@link Direction Directions} and all their relevant {@link FaceAdjacency} values
     */
    public Map<Direction, EnumSet<FaceAdjacency>> computeFaceConnections(BlockAndTintGetter level, BlockPos pos, Block selfBlock) {
        // A map of directions and all their relevant adjacent blocks
        Map<Direction, EnumSet<FaceAdjacency>> faceConnections = new EnumMap<>(Direction.class);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (Direction faceDirection : ALL_DIRECTIONS) {
            // If there is a block in front of the face we skip
            mutablePos.set(pos).move(faceDirection);
            if (level.getBlockState(mutablePos).is(selfBlock)) continue;

            EnumSet<FaceAdjacency> set = EnumSet.noneOf(FaceAdjacency.class);

            // We check all axis-aligned blocks
            for (FaceAdjacency adj : FaceAdjacency.axisAlignedValues()) {
                BlockPos offset = FaceAdjacency.getOffset(faceDirection, adj);
                // Checks if there is a block adjacent
                mutablePos.set(pos).move(offset);
                if (level.getBlockState(mutablePos).is(selfBlock)) {
                    // Ensures the block isn't covered by another
                    mutablePos.move(faceDirection);
                    if (!level.getBlockState(mutablePos).is(selfBlock)) {
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
                if (level.getBlockState(mutablePos).is(selfBlock)) {
                    // Ensures the block isn't covered by another
                    mutablePos.move(faceDirection);
                    if (!level.getBlockState(mutablePos).is(selfBlock)) {
                        set.add(adj);
                    }
                }
            }

            // Lastly we store each face direction and its valid adjacent blocks
            faceConnections.put(faceDirection, set);
        }


        EnumMap<Direction, FaceAdjacency.Mutation> mutations = EnumMapUtils.enumMap(Direction.class, EnumMapUtils.entry(Direction.NORTH, FaceAdjacency.Mutation.INVERTED));

        if (!mutations.isEmpty()) {
            EnumSet<FaceAdjacency> tempSet = EnumSet.noneOf(FaceAdjacency.class);
            for (Direction direction : mutations.keySet()) {
                FaceAdjacency.Mutation mutation = mutations.get(direction);
                EnumSet<FaceAdjacency> adjSet = faceConnections.get(direction);
                tempSet.clear();
                for (FaceAdjacency adj : adjSet)
                    tempSet.add(adj.transform(mutation));
                faceConnections.put(direction, tempSet);
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

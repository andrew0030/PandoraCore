package com.github.andrew0030.pandora_core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConnectedTextureBakedModel implements BakedModel {
    private final BakedModel originalModel;
    public static final ModelProperty<Map<Direction, EnumSet<FaceAdjacency>>> FACE_CONNECTIONS = new ModelProperty<>();
    private static final List<Direction> ALL_DIRECTIONS = List.of(Direction.values());
    public static final Map<Integer, Integer> CTM_LOOKUP = Map.ofEntries(
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
    public static final Map<FaceAdjacency, Integer> FACE_ADJACENCY_TO_BIT = Map.of(
            FaceAdjacency.TOP_LEFT, 1,
            FaceAdjacency.TOP, 2,
            FaceAdjacency.TOP_RIGHT, 4,
            FaceAdjacency.LEFT, 8,
            FaceAdjacency.RIGHT, 16,
            FaceAdjacency.BOTTOM_LEFT, 32,
            FaceAdjacency.BOTTOM, 64,
            FaceAdjacency.BOTTOM_RIGHT, 128
    );

    public ConnectedTextureBakedModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        // A map of directions and all their relevant adjacent blocks
        Map<Direction, EnumSet<FaceAdjacency>> faceConnections = new EnumMap<>(Direction.class);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Block selfBlock = state.getBlock();

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

        return ModelData.builder().with(FACE_CONNECTIONS, faceConnections).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
        if (state == null || side == null) {
            return originalModel.getQuads(state, side, rand, data, renderType);
        }

        // fetch base quads and sheet sprite
        List<BakedQuad> base = originalModel.getQuads(state, side, rand, data, renderType);
        TextureAtlasSprite sheet = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(TextureAtlas.LOCATION_BLOCKS)
                .getSprite(new ResourceLocation("pandora_core:block/connected_block_sheet"));

        int mask = 0;
        Map<Direction, EnumSet<FaceAdjacency>> set = data.get(FACE_CONNECTIONS);
        if (set != null) {
            EnumSet<FaceAdjacency> faceSet = set.getOrDefault(side, EnumSet.noneOf(FaceAdjacency.class));
            for (FaceAdjacency adj : faceSet) {
                mask |= FACE_ADJACENCY_TO_BIT.get(adj);
            }
        }
        int tileIndex = CTM_LOOKUP.getOrDefault(mask, 0);

        // compute U/V offsets for a 12×4 grid
        final int cols = 12, rows = 4;
        float uSize = (sheet.getU1() - sheet.getU0()) / cols;
        float vSize = (sheet.getV1() - sheet.getV0()) / rows;

        int row = tileIndex / cols;
        int col = tileIndex % cols;

        float uOffset = sheet.getU0() + col * uSize;
        float vOffset = sheet.getV0() + row * vSize;

        // remap each quad into that cell
        List<BakedQuad> out = new ArrayList<>(base.size());
        for (BakedQuad quad : base) {
            out.add(remapToCell(quad, sheet, uOffset, vOffset, uSize, vSize));
        }
        return out;
    }

    private BakedQuad remapToCell(BakedQuad quad, TextureAtlasSprite sprite, float uMin, float vMin, float uSize, float vSize) {
        int[] data = quad.getVertices().clone();

        float oldU0 = quad.getSprite().getU0(), oldU1 = quad.getSprite().getU1();
        float oldV0 = quad.getSprite().getV0(), oldV1 = quad.getSprite().getV1();

        for (int i = 0; i < 4; i++) {
            int b = i * 8;
            float u = Float.intBitsToFloat(data[b + 4]);
            float v = Float.intBitsToFloat(data[b + 5]);

            // normalize within old sprite
            float uNorm = (u - oldU0) / (oldU1 - oldU0);
            float vNorm = (v - oldV0) / (oldV1 - oldV0);

            // map into target cell
            float newU = uMin + uNorm * uSize;
            float newV = vMin + vNorm * vSize;

            data[b + 4] = Float.floatToRawIntBits(newU);
            data[b + 5] = Float.floatToRawIntBits(newV);
        }

        return new BakedQuad(data, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    // Delegate other BakedModel methods
    @Override public boolean useAmbientOcclusion() { return originalModel.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return originalModel.isGui3d(); }
    @Override public boolean usesBlockLight() { return originalModel.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return originalModel.isCustomRenderer(); }
    @Override public @NotNull TextureAtlasSprite getParticleIcon() { return originalModel.getParticleIcon(); }
    @Override public @NotNull ItemTransforms getTransforms() { return originalModel.getTransforms(); }
    @Override public @NotNull ItemOverrides getOverrides() { return originalModel.getOverrides(); }

    /**
     * Represents the relative position of neighboring blocks around a face in a 3x3 grid layout,
     * used for Connected Texture Mapping (CTM) logic.
     * <p>
     * Each enum constant corresponds to a direction in the 2D plane surrounding a face:
     * <pre>
     * Bit layout:
     *
     *  1   2   4      // TOP_LEFT, TOP, TOP_RIGHT
     *  8   x  16      // LEFT,     x,   RIGHT
     * 32  64 128      // BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT
     * </pre>
     * <p>
     * These bit values are used to compute unique connection keys for texture lookup,
     * allowing combinations of adjacent block presence to be compactly encoded and decoded.
     * <p>
     * Each enum entry also stores its relative offset (dx, dy).
     */
    public enum FaceAdjacency {
        TOP_LEFT(1, 1),
        TOP(0, 1),
        TOP_RIGHT(-1, 1),
        LEFT(1, 0),
        RIGHT(-1, 0),
        BOTTOM_LEFT(1, -1),
        BOTTOM(0, -1),
        BOTTOM_RIGHT(-1, -1);

        private static final EnumSet<FaceAdjacency> AXIS_ALIGNED = EnumSet.of(TOP, LEFT, RIGHT, BOTTOM);
        private static final EnumSet<FaceAdjacency> DIAGONAL = EnumSet.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
        private static final Map<FaceAdjacency, EnumSet<FaceAdjacency>> DIAGONAL_DEPENDENCIES = Map.of(
                TOP_LEFT, EnumSet.of(TOP, LEFT),
                TOP_RIGHT, EnumSet.of(TOP, RIGHT),
                BOTTOM_LEFT, EnumSet.of(BOTTOM, LEFT),
                BOTTOM_RIGHT, EnumSet.of(BOTTOM, RIGHT)
            );
        private static final EnumMap<Direction, EnumMap<FaceAdjacency, BlockPos>> OFFSET_MAP = new EnumMap<>(Direction.class);
        private final int dx, dy;

        static {
            // Computes and caches all offsets based on all directions
            for (Direction dir : Direction.values()) {
                EnumMap<FaceAdjacency, BlockPos> faceMap = new EnumMap<>(FaceAdjacency.class);
                for (FaceAdjacency adj : FaceAdjacency.values())
                    faceMap.put(adj, computeOffset(adj, dir));
                OFFSET_MAP.put(dir, faceMap);
            }
        }

        FaceAdjacency(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        /**
         * Computes the relative {@link BlockPos} offset for a given face direction and adjacency.
         *
         * @param adj  The {@link FaceAdjacency} being transformed.
         * @param face The direction of the face from which the offset is projected.
         * @return A new {@link BlockPos} representing the 3D offset.
         */
        private static BlockPos computeOffset(FaceAdjacency adj, Direction face) {
            int dx = adj.dx;
            int dy = adj.dy;
            return switch (face) {
                case NORTH -> new BlockPos(dx, dy, 0);
                case SOUTH -> new BlockPos(-dx, dy, 0);
                case EAST  -> new BlockPos(0, dy, dx);
                case WEST  -> new BlockPos(0, dy, -dx);
                case UP    -> new BlockPos(-dx, 0, -dy);
                case DOWN  -> new BlockPos(-dx, 0, dy);
            };
        }

        /**
         * Returns a cached {@link BlockPos} offset for the specified {@link Direction} and {@link FaceAdjacency}.
         *
         * @param face The direction of the face being queried.
         * @param adj  The adjacency direction relative to the face.
         * @return A precomputed {@link BlockPos} representing the 3D offset.
         *
         * @implNote All offsets are precomputed and cached for performance.
         */
        public static BlockPos getOffset(Direction face, FaceAdjacency adj) {
            return OFFSET_MAP.get(face).get(adj);
        }

        /**
         * @return A cached {@link EnumSet} of all axis-aligned {@link FaceAdjacency} values.
         */
        public static EnumSet<FaceAdjacency> axisAlignedValues() {
            return AXIS_ALIGNED;
        }

        /**
         * @return A cached {@link EnumSet} of all diagonal {@link FaceAdjacency} values.
         */
        public static EnumSet<FaceAdjacency> diagonalValues() {
            return DIAGONAL;
        }

        /**
         * @return A cached {@link EnumSet} containing all diagonal {@link FaceAdjacency} dependencies.
         */
        public static EnumSet<FaceAdjacency> getDiagonalDependencies(FaceAdjacency diagonal) {
            return DIAGONAL_DEPENDENCIES.getOrDefault(diagonal, EnumSet.noneOf(FaceAdjacency.class));
        }
    }
}
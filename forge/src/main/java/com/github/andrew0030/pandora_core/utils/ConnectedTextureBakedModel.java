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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConnectedTextureBakedModel implements BakedModel {
    private final BakedModel originalModel;
    public static final ModelProperty<Map<Direction, EnumSet<FaceAdjacency>>> FACE_CONNECTIONS = new ModelProperty<>();
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
        Map<Direction, EnumSet<FaceAdjacency>> faceConnections = new EnumMap<>(Direction.class);

        for (Direction face : Direction.values()) {
            EnumSet<FaceAdjacency> set = EnumSet.noneOf(FaceAdjacency.class);
            EnumSet<FaceAdjacency> directConnections = EnumSet.noneOf(FaceAdjacency.class); // Store axis-aligned only

            for (FaceAdjacency adj : FaceAdjacency.values()) {
                BlockPos offset = projectRelative(face, adj.dx, adj.dy);
                if (level.getBlockState(pos.offset(offset)).is(state.getBlock())) {

                    BlockPos faceOffsetPos = pos.offset(offset).relative(face);
                    BlockState faceBlock = level.getBlockState(faceOffsetPos);
                    // Skip this face entirely if it's blocked by the same type
                    if (faceBlock.is(state.getBlock()))
                        continue;

                    // Save direct connections immediately
                    if (adj.axisAligned) {
                        set.add(adj);
                        directConnections.add(adj);
                    }
                }
            }

            // Now recheck diagonals
            for (FaceAdjacency adj : FaceAdjacency.values()) {
                if (adj.diagonal) {
                    FaceAdjacency dir1 = FaceAdjacency.fromOffsets(adj.dx, 0);
                    FaceAdjacency dir2 = FaceAdjacency.fromOffsets(0, adj.dy);
                    if (directConnections.contains(dir1) && directConnections.contains(dir2)) {
                        BlockPos offset = projectRelative(face, adj.dx, adj.dy);
                        if (level.getBlockState(pos.offset(offset)).is(state.getBlock())) {

                            BlockPos faceOffsetPos = pos.offset(offset).relative(face);
                            BlockState faceBlock = level.getBlockState(faceOffsetPos);
                            // Skip this face entirely if it's blocked by the same type
                            if (faceBlock.is(state.getBlock()))
                                continue;

                            set.add(adj);
                        }
                    }
                }
            }

            faceConnections.put(face, set);
        }

        return ModelData.builder().with(FACE_CONNECTIONS, faceConnections).build();
    }

    public static BlockPos projectRelative(Direction face, int dx, int dy) {
        return switch (face) {
            case NORTH -> new BlockPos(dx, dy, 0);
            case SOUTH -> new BlockPos(-dx, dy, 0);
            case EAST  -> new BlockPos(0, dy, dx);
            case WEST  -> new BlockPos(0, dy, -dx);
            case UP    -> new BlockPos(-dx, 0, -dy);
            case DOWN  -> new BlockPos(-dx, 0, dy);
        };
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
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
        EnumSet<FaceAdjacency> faceSet = data.get(FACE_CONNECTIONS).getOrDefault(side, EnumSet.noneOf(FaceAdjacency.class));
        for (FaceAdjacency adj : faceSet) {
            mask |= FACE_ADJACENCY_TO_BIT.get(adj);
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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    // Delegate other BakedModel methods
    @Override public boolean useAmbientOcclusion() { return originalModel.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return originalModel.isGui3d(); }
    @Override public boolean usesBlockLight() { return originalModel.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return originalModel.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return originalModel.getParticleIcon(); }
    @Override public ItemTransforms getTransforms() { return originalModel.getTransforms(); }
    @Override public ItemOverrides getOverrides() { return originalModel.getOverrides(); }

    public enum FaceAdjacency {
        TOP_LEFT(1, 1, false, true),
        TOP(0, 1, true, false),
        TOP_RIGHT(-1, 1, false, true),
        LEFT(1, 0, true, false),
        RIGHT(-1, 0, true, false),
        BOTTOM_LEFT(1, -1, false, true),
        BOTTOM(0, -1, true, false),
        BOTTOM_RIGHT(-1, -1, false, true);

        public static final EnumSet<FaceAdjacency> AXIS_ALIGNED = EnumSet.of(TOP, LEFT, RIGHT, BOTTOM);
        public static final EnumSet<FaceAdjacency> DIAGONAL = EnumSet.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
        public final int dx, dy;
        public final boolean axisAligned, diagonal;

        FaceAdjacency(int dx, int dy, boolean axisAligned, boolean diagonal) {
            this.dx = dx;
            this.dy = dy;
            this.axisAligned = axisAligned;
            this.diagonal = diagonal;
        }

        public BlockPos getOffset(Direction face) {
            return switch (face) {
                case NORTH -> new BlockPos(dx, dy, 0);
                case SOUTH -> new BlockPos(-dx, dy, 0);
                case EAST  -> new BlockPos(0, dy, dx);
                case WEST  -> new BlockPos(0, dy, -dx);
                case UP    -> new BlockPos(-dx, 0, -dy);
                case DOWN  -> new BlockPos(-dx, 0, dy);
            };
        }

        public static FaceAdjacency fromOffsets(int dx, int dy) {
            for (FaceAdjacency a : values()) {
                if (a.dx == dx && a.dy == dy) return a;
            }
            return null;
        }

        public static EnumSet<FaceAdjacency> axisAlignedValues() {
            return AXIS_ALIGNED;
        }

        public static EnumSet<FaceAdjacency> diagonalValues() {
            return DIAGONAL;
        }
    }
}
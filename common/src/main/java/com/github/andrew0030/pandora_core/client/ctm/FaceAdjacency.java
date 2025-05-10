package com.github.andrew0030.pandora_core.client.ctm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

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
    TOP_LEFT     ( 1,  1,   1),
    TOP          ( 0,  1,   2),
    TOP_RIGHT    (-1,  1,   4),
    LEFT         ( 1,  0,   8),
    RIGHT        (-1,  0,  16),
    BOTTOM_LEFT  ( 1, -1,  32),
    BOTTOM       ( 0, -1,  64),
    BOTTOM_RIGHT (-1, -1, 128);

    private static final EnumSet<FaceAdjacency> AXIS_ALIGNED = EnumSet.of(TOP, LEFT, RIGHT, BOTTOM);
    private static final EnumSet<FaceAdjacency> DIAGONAL = EnumSet.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
    private static final Map<FaceAdjacency, EnumSet<FaceAdjacency>> DIAGONAL_DEPENDENCIES = Map.of(
            TOP_LEFT, EnumSet.of(TOP, LEFT),
            TOP_RIGHT, EnumSet.of(TOP, RIGHT),
            BOTTOM_LEFT, EnumSet.of(BOTTOM, LEFT),
            BOTTOM_RIGHT, EnumSet.of(BOTTOM, RIGHT)
    );
    private static final EnumMap<Direction, EnumMap<FaceAdjacency, BlockPos>> OFFSET_MAP = new EnumMap<>(Direction.class);
    private final int dx, dy, bit;

    static {
        // Computes and caches all offsets based on all directions
        for (Direction dir : Direction.values()) {
            EnumMap<FaceAdjacency, BlockPos> faceMap = new EnumMap<>(FaceAdjacency.class);
            for (FaceAdjacency adj : FaceAdjacency.values())
                faceMap.put(adj, computeOffset(adj, dir));
            OFFSET_MAP.put(dir, faceMap);
        }
    }

    FaceAdjacency(int dx, int dy, int bit) {
        this.dx = dx;
        this.dy = dy;
        this.bit = bit;
    }

    /**
     * @return The bit value.
     */
    public int getBit() {
        return this.bit;
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
package com.github.andrew0030.pandora_core.client.ctm;

import com.github.andrew0030.pandora_core.utils.collection.EnumMapUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.EnumSet;

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
    TOP_LEFT     ( 1,  1,   1, false),
    TOP          ( 0,  1,   2, true ),
    TOP_RIGHT    (-1,  1,   4, false),
    LEFT         ( 1,  0,   8, true ),
    RIGHT        (-1,  0,  16, true ),
    BOTTOM_LEFT  ( 1, -1,  32, false),
    BOTTOM       ( 0, -1,  64, true ),
    BOTTOM_RIGHT (-1, -1, 128, false);

    private static final EnumSet<FaceAdjacency> AXIS_ALIGNED = EnumSet.of(TOP, LEFT, RIGHT, BOTTOM);
    private static final EnumSet<FaceAdjacency> DIAGONAL = EnumSet.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
    private static final FaceAdjacency[][] ROTATIONS = new FaceAdjacency[3][8];
    private static final EnumMap<Direction, EnumMap<FaceAdjacency, BlockPos>> OFFSET_MAP = new EnumMap<>(Direction.class);
    private EnumSet<FaceAdjacency> diagonalDependencies;
    private final int dx, dy, bit;
    private final boolean isAxisAligned;

    static {
        // CLOCKWISE
        ROTATIONS[0][0] = BOTTOM_LEFT;
        ROTATIONS[0][1] = LEFT;
        ROTATIONS[0][2] = TOP_LEFT;
        ROTATIONS[0][3] = BOTTOM;
        ROTATIONS[0][4] = TOP;
        ROTATIONS[0][5] = BOTTOM_RIGHT;
        ROTATIONS[0][6] = RIGHT;
        ROTATIONS[0][7] = TOP_RIGHT;
        // INVERTED (180°)
        ROTATIONS[1][0] = BOTTOM_RIGHT;
        ROTATIONS[1][1] = BOTTOM;
        ROTATIONS[1][2] = BOTTOM_LEFT;
        ROTATIONS[1][3] = RIGHT;
        ROTATIONS[1][4] = LEFT;
        ROTATIONS[1][5] = TOP_RIGHT;
        ROTATIONS[1][6] = TOP;
        ROTATIONS[1][7] = TOP_LEFT;
        // COUNTER_CLOCKWISE
        ROTATIONS[2][0] = TOP_RIGHT;
        ROTATIONS[2][1] = RIGHT;
        ROTATIONS[2][2] = BOTTOM_RIGHT;
        ROTATIONS[2][3] = TOP;
        ROTATIONS[2][4] = BOTTOM;
        ROTATIONS[2][5] = TOP_LEFT;
        ROTATIONS[2][6] = LEFT;
        ROTATIONS[2][7] = BOTTOM_LEFT;

        // Computes and caches all offsets based on all directions
        for (Direction dir : Direction.values()) {
            EnumMap<FaceAdjacency, BlockPos> faceMap = new EnumMap<>(FaceAdjacency.class);
            for (FaceAdjacency adj : FaceAdjacency.values())
                faceMap.put(adj, computeOffset(adj, dir));
            OFFSET_MAP.put(dir, faceMap);
        }
    }

    FaceAdjacency(int dx, int dy, int bit, boolean isAxisAligned) {
        this.dx = dx;
        this.dy = dy;
        this.bit = bit;
        this.isAxisAligned = isAxisAligned;
    }

    /**
     * @return The bit value.
     */
    public int getBit() {
        return this.bit;
    }

    public boolean isAxisAligned() {
        return this.isAxisAligned;
    }

    /**
     * Returns the transformed variant of this {@link FaceAdjacency} based on the specified {@link Mutation}.
     * <p>
     * This is used to transform adjacency direction checks according to rotation or mirroring operations
     * (e.g., when a block face is rotated 90°, mirrored, or flipped).
     * <p>
     * Internally uses a precomputed lookup table for high performance.
     *
     * @param mutation The rotation or transformation to apply.
     * @return The resulting {@link FaceAdjacency} after applying the mutation.
     */
    // TODO maybe add flipping?
    public FaceAdjacency transform(Mutation mutation) {
        if (mutation == Mutation.NONE)
            return this;
        return ROTATIONS[mutation.ordinal()][this.ordinal()];
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
     * @return A lazily cached {@link EnumSet} containing all diagonal {@link FaceAdjacency} dependencies.
     */
    public static EnumSet<FaceAdjacency> getDiagonalDependencies(FaceAdjacency diagonal) {
        if (diagonal.diagonalDependencies == null) {
            switch (diagonal) {
                case TOP_LEFT -> diagonal.diagonalDependencies = EnumSet.of(TOP, LEFT);
                case TOP_RIGHT -> diagonal.diagonalDependencies = EnumSet.of(TOP, RIGHT);
                case BOTTOM_LEFT -> diagonal.diagonalDependencies = EnumSet.of(BOTTOM, LEFT);
                case BOTTOM_RIGHT -> diagonal.diagonalDependencies = EnumSet.of(BOTTOM, RIGHT);
                default -> diagonal.diagonalDependencies = EnumSet.noneOf(FaceAdjacency.class);
            }
        }

        return diagonal.diagonalDependencies;
    }

    /**
     * Represents directional mutations applied to a {@link FaceAdjacency}, used in
     * rotating or mirroring face connection detection.
     * <ul>
     *   <li>{@link #ROT_CW}   – Detection logic is rotated 90° clockwise.</li>
     *   <li>{@link #INVERTED} – Detection logic is rotated 180° (upside down).</li>
     *   <li>{@link #ROT_CCW}  – Detection logic is rotated 90° counter-clockwise.</li>
     *   <li>{@link #NONE}     – Detection logic is kept as-is.</li>
     * </ul>
     */
    // TODO maybe add flipping?
    public enum Mutation {
        ROT_CW,
        INVERTED,
        ROT_CCW,
        NONE;
    }
}
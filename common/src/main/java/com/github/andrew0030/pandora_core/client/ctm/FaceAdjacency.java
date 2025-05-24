package com.github.andrew0030.pandora_core.client.ctm;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

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
 */
public enum FaceAdjacency {
    TOP_LEFT     (  1),
    TOP          (  2),
    TOP_RIGHT    (  4),
    LEFT         (  8),
    RIGHT        ( 16),
    BOTTOM_LEFT  ( 32),
    BOTTOM       ( 64),
    BOTTOM_RIGHT (128);

    private static final FaceAdjacency[][] ROTATIONS = new FaceAdjacency[3][8];
    private static final Int2ObjectMap<BlockPos> CROSS_OFFSET_MAP = new Int2ObjectArrayMap<>();
    private static final Int2ObjectMap<BlockPos> EXTENDED_CROSS_OFFSET_MAP = new Int2ObjectArrayMap<>();
    private static final Int2ObjectMap<BlockPos> OUTERMOST_OFFSET_MAP = new Int2ObjectArrayMap<>();
    private EnumSet<FaceAdjacency> diagonalDependencies;
    private final int bit;

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

        //   North Y: 1       North Y: 0         North Y: -1
        //  ____________    _______________    _______________
        // | 0 | 1 | 2 |   | 9  | 10 | 11 |   | 17 | 18 | 19 |
        // | 3 | 4 | 5 |   | 12 |    | 13 |   | 20 | 21 | 22 |  East
        // | 6 | 7 | 8 |   | 14 | 15 | 16 |   | 23 | 24 | 25 |
        // ------------    ---------------    ---------------
        //
        // Computes and caches all offsets
        BlockPos pos = new BlockPos(0, 0, 0);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        // The standard 3D cross required for all basic CTM that check neighbors
        CROSS_OFFSET_MAP.put(21, mutablePos.set(pos).move(Direction.DOWN).immutable());
        CROSS_OFFSET_MAP.put(4,  mutablePos.set(pos).move(Direction.UP).immutable());
        CROSS_OFFSET_MAP.put(10, mutablePos.set(pos).move(Direction.NORTH).immutable());
        CROSS_OFFSET_MAP.put(15, mutablePos.set(pos).move(Direction.SOUTH).immutable());
        CROSS_OFFSET_MAP.put(12, mutablePos.set(pos).move(Direction.WEST).immutable());
        CROSS_OFFSET_MAP.put(13, mutablePos.set(pos).move(Direction.EAST).immutable());
        // If we need to check diagonals or "in front of" we need the blocks next to the cross
        // Top Layer
        EXTENDED_CROSS_OFFSET_MAP.put(1,  mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(7,  mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(3,  mutablePos.set(pos).move(Direction.UP).move(Direction.WEST).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(5,  mutablePos.set(pos).move(Direction.UP).move(Direction.EAST).immutable());
        // Middle Layer
        EXTENDED_CROSS_OFFSET_MAP.put(9,  mutablePos.set(pos).move(Direction.NORTH).move(Direction.WEST).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(11, mutablePos.set(pos).move(Direction.NORTH).move(Direction.EAST).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(14, mutablePos.set(pos).move(Direction.SOUTH).move(Direction.WEST).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(16, mutablePos.set(pos).move(Direction.SOUTH).move(Direction.EAST).immutable());
        // Bottom Layer
        EXTENDED_CROSS_OFFSET_MAP.put(18, mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(24, mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(20, mutablePos.set(pos).move(Direction.DOWN).move(Direction.WEST).immutable());
        EXTENDED_CROSS_OFFSET_MAP.put(22, mutablePos.set(pos).move(Direction.DOWN).move(Direction.EAST).immutable());
        // If we need to check "in front of" diagonals we require the outermost edge blocks
        // Top Layer
        OUTERMOST_OFFSET_MAP.put(0,  mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH).move(Direction.WEST).immutable());
        OUTERMOST_OFFSET_MAP.put(2,  mutablePos.set(pos).move(Direction.UP).move(Direction.NORTH).move(Direction.EAST).immutable());
        OUTERMOST_OFFSET_MAP.put(6,  mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH).move(Direction.WEST).immutable());
        OUTERMOST_OFFSET_MAP.put(8,  mutablePos.set(pos).move(Direction.UP).move(Direction.SOUTH).move(Direction.EAST).immutable());
        // Bottom Layer
        OUTERMOST_OFFSET_MAP.put(17, mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH).move(Direction.WEST).immutable());
        OUTERMOST_OFFSET_MAP.put(19, mutablePos.set(pos).move(Direction.DOWN).move(Direction.NORTH).move(Direction.EAST).immutable());
        OUTERMOST_OFFSET_MAP.put(23, mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH).move(Direction.WEST).immutable());
        OUTERMOST_OFFSET_MAP.put(25, mutablePos.set(pos).move(Direction.DOWN).move(Direction.SOUTH).move(Direction.EAST).immutable());
    }

    FaceAdjacency(int bit) {
        this.bit = bit;
    }

    /** @return The bit value. */
    public int getBit() {
        return this.bit;
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
     * A map used to check blocks in a 3D cross pattern around the block.
     * @return An {@link Int2ObjectMap} containing {@link BlockPos} offsets.
     */
    public static Int2ObjectMap<BlockPos> getCrossOffsets() {
        return CROSS_OFFSET_MAP;
    }

    /**
     * A map used to check blocks adjacent to a 3D cross pattern around the block.
     * @return An {@link Int2ObjectMap} containing {@link BlockPos} offsets.
     */
    public static Int2ObjectMap<BlockPos> getExtendedCrossOffsets() {
        return EXTENDED_CROSS_OFFSET_MAP;
    }

    /**
     * A map used to check the outermost blocks around the block.
     * @return An {@link Int2ObjectMap} containing {@link BlockPos} offsets.
     */
    public static Int2ObjectMap<BlockPos> getOutermostOffsets() {
        return OUTERMOST_OFFSET_MAP;
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
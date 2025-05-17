package com.github.andrew0030.pandora_core.client.ctm;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ForgeCTMModel extends BaseCTMModel {
    private static final ModelProperty<EnumMap<Direction, EnumSet<FaceAdjacency>>> FACE_CONNECTIONS = new ModelProperty<>();

    public ForgeCTMModel(BakedModel model, CTMSpriteResolver spriteResolver, CTMDataResolver dataResolver) {
        super(model, spriteResolver, dataResolver);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        EnumMap<Direction, EnumSet<FaceAdjacency>> faceConnections = this.computeFaceConnections(level, pos, state);
        return ModelData.builder().with(FACE_CONNECTIONS, faceConnections).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
        if (state == null)
            return this.model.getQuads(null, side, rand, data, renderType);

        List<BakedQuad> base = this.model.getQuads(state, side, rand, data, renderType);
        List<BakedQuad> out = new ArrayList<>(base.size());
        for (BakedQuad quad : base) {
            // Calculates the texture index based on the quad's direction and the adjacent values
            int mask = 0;
            EnumMap<Direction, EnumSet<FaceAdjacency>> map = data.get(FACE_CONNECTIONS);
            if (map != null)
                for (FaceAdjacency adj : map.get(quad.getDirection()))
                    mask |= adj.getBit();
            int tileIndex = CTM_LOOKUP.getOrDefault(mask, 0);

            // Gets the replacement texture, or returns early if there is none
            CTMSpriteResolver.SpriteResultHolder result = this.spriteResolver.get(quad.getSprite(), tileIndex);
            if (result == null) {
                out.add(quad);
                continue; // If there is no texture to be replaced, we return the original and skip further logic
            }
            TextureAtlasSprite sheet = result.get();

            // Computes U/V offsets for a 12×4 grid
            boolean isSingleSprite = result.isMissing() || this.spriteResolver.usesMultipleSprites(quad.getSprite());
            // TODO make this a thing provided by the CTM type
            final int cols = isSingleSprite ? 1 : 12;
            final int rows = isSingleSprite ? 1 : 4;
            float uSize = (sheet.getU1() - sheet.getU0()) / cols;
            float vSize = (sheet.getV1() - sheet.getV0()) / rows;

            int row = isSingleSprite ? 0 : (tileIndex / cols);
            int col = isSingleSprite ? 0 : (tileIndex % cols);

            float uOffset = sheet.getU0() + col * uSize;
            float vOffset = sheet.getV0() + row * vSize;

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

            float newU = this.remapUV(u, oldU0, oldU1, uMin, uMin + uSize);
            float newV = this.remapUV(v, oldV0, oldV1, vMin, vMin + vSize);

            data[b + 4] = Float.floatToRawIntBits(newU);
            data[b + 5] = Float.floatToRawIntBits(newV);
        }

        return new BakedQuad(data, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }
}
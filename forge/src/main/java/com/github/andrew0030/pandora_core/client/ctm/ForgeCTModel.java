package com.github.andrew0030.pandora_core.client.ctm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ForgeCTModel extends BaseCTModel {
    public static final ModelProperty<Map<Direction, EnumSet<FaceAdjacency>>> FACE_CONNECTIONS = new ModelProperty<>();

    public ForgeCTModel(BakedModel model) {
        super(model);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        Map<Direction, EnumSet<FaceAdjacency>> faceConnections = this.computeFaceConnections(level, pos, state.getBlock());
        return ModelData.builder().with(FACE_CONNECTIONS, faceConnections).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource rand, @NotNull ModelData data, RenderType renderType) {
        if (state == null || side == null) {
            return this.model.getQuads(state, side, rand, data, renderType);
        }

        // fetch base quads and sheet sprite
        List<BakedQuad> base = this.model.getQuads(state, side, rand, data, renderType);
        TextureAtlasSprite sheet = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(TextureAtlas.LOCATION_BLOCKS)
                .getSprite(new ResourceLocation("pandora_core:block/connected_block_sheet"));

        int mask = 0;
        Map<Direction, EnumSet<FaceAdjacency>> set = data.get(FACE_CONNECTIONS);
        if (set != null) {
            EnumSet<FaceAdjacency> faceSet = set.getOrDefault(side, EnumSet.noneOf(FaceAdjacency.class));
            for (FaceAdjacency adj : faceSet) {
                mask |= adj.getBit();
            }
        }
        int tileIndex = CTM_LOOKUP.getOrDefault(mask, 0);

        // computes U/V offsets for a 12×4 grid
        // TODO make this a thing provided by the CTM type
        final int cols = 12, rows = 4;
        float uSize = (sheet.getU1() - sheet.getU0()) / cols;
        float vSize = (sheet.getV1() - sheet.getV0()) / rows;

        int row = tileIndex / cols;
        int col = tileIndex % cols;

        float uOffset = sheet.getU0() + col * uSize;
        float vOffset = sheet.getV0() + row * vSize;

        // remaps each quad into that cell
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
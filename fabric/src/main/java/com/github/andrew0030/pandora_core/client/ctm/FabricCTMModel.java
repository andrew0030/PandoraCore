package com.github.andrew0030.pandora_core.client.ctm;

import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FabricCTMModel extends BaseCTMModel {
    private static final EnumSet<FaceAdjacency> EMPTY_SET = EnumSet.noneOf(FaceAdjacency.class);

    public FabricCTMModel(BakedModel model, CTMSpriteResolver spriteResolver, CTMDataResolver dataResolver) {
        super(model, spriteResolver, dataResolver);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        Map<Direction, EnumSet<FaceAdjacency>> faceConnections = this.computeFaceConnections(level, pos, state);
        SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));
        context.pushTransform(quad -> {
            // Gets the replacement texture, or returns early if there is none
            TextureAtlasSprite original = spriteFinder.find(quad);
            CTMSpriteResolver.SpriteResultHolder result = this.spriteResolver.get(original);
            if (result == null)
                return true; // If there is no texture to be replaced, we don't mutate the quad and skip further logic
            TextureAtlasSprite sheet = result.get();

            // Gets the relevant adjacency set based on the quad's light face
            EnumSet<FaceAdjacency> set = faceConnections.get(quad.lightFace());

            // Calculates the texture index based on the quad's direction and the adjacent values
            int mask = 0;
            for (FaceAdjacency adj : set)
                mask |= adj.getBit();
            int tileIndex = CTM_LOOKUP.getOrDefault(mask, 0);

            // Computes U/V offsets for a 12×4 grid
            // TODO make this a thing provided by the CTM type
            final int cols = result.isMissing() ? 1 : 12;
            final int rows = result.isMissing() ? 1 : 4;
            float uSize = (sheet.getU1() - sheet.getU0()) / cols;
            float vSize = (sheet.getV1() - sheet.getV0()) / rows;

            int row = result.isMissing() ? 0 : (tileIndex / cols);
            int col = result.isMissing() ? 0 : (tileIndex % cols);

            float uOffset = sheet.getU0() + col * uSize;
            float vOffset = sheet.getV0() + row * vSize;

            float oldU0 = original.getU0(), oldU1 = original.getU1();
            float oldV0 = original.getV0(), oldV1 = original.getV1();

            for (int i = 0; i < 4; i++) {
                float remappedU = remapUV(quad.u(i), oldU0, oldU1, uOffset, uOffset + uSize);
                float remappedV = remapUV(quad.v(i), oldV0, oldV1, vOffset, vOffset + vSize);
                quad.uv(i, remappedU, remappedV);
            }

            return true;
        });

        this.model.emitBlockQuads(level, state, pos, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
        return List.of();
    }
}
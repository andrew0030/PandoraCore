package com.github.andrew0030.pandora_core.test.block_entities;

import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.render.dynamic.PaCoDynamicItemRenderer;
import com.github.andrew0030.pandora_core.registry.test.PaCoBlocks;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TestDynamicItemRenderer extends PaCoDynamicItemRenderer {
    private static final TestBlockEntity TEST_BLOCK_ENTITY = new TestBlockEntity(BlockPos.ZERO, PaCoBlocks.TEST.get().defaultBlockState());

    public TestDynamicItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }

    @Override
    public boolean ignoreAttachedTransforms(ItemStack stack, ItemDisplayContext context, int packedLight, int packedOverlay, BakedModel model) {
        if (stack.is(PaCoBlocks.INSTANCING_TEST.get().asItem()))
            return false;
        return !stack.is(PaCoBlocks.TEST.get().asItem());
    }

    @Override
    public void render(ItemStack stack, float partialTick, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model) {
        Item item = stack.getItem();
        if (item == PaCoBlocks.TEST.get().asItem()) {
            this.renderDispatcher.renderItem(TEST_BLOCK_ENTITY, poseStack, buffer, packedLight, packedOverlay);
        } else if (context == ItemDisplayContext.GUI) {
            // Non-3D model items use flat light, but since our model is 3D we need to adjust it
            boolean usesBlockLight = model.usesBlockLight();
            if (!usesBlockLight)
                Lighting.setupFor3DItems();

            poseStack.pushPose();
            poseStack.translate(0, 0, -1);
            float time = PaCoClientTicker.getGame() + partialTick;
            float speed = 0.2F;
            float amplitudeDegrees = 15.0F;
            float phase = time * speed;
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(phase) * amplitudeDegrees));
            poseStack.mulPose(Axis.YN.rotationDegrees(Mth.sin(phase) * amplitudeDegrees));
            poseStack.translate(0, 0, 1);

            // Centers the block (normally this is done by MC, but for these we ignore the item transforms)
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            this.renderDispatcher.renderItem(TEST_BLOCK_ENTITY, poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();

            // If the item used flat light we need to end the batch to render our model
            // with the 3D light, and then afterward reset the light to flat for the item.
            if (!usesBlockLight) {
                if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
                    bufferSource.endBatch();
                Lighting.setupForFlatItems();
            }
        }
    }

    @Override
    public void applyPostRenderTransforms(ItemStack stack, float partialTick, ItemDisplayContext context, PoseStack poseStack) {
        if (context == ItemDisplayContext.GUI) {
            float time = PaCoClientTicker.getGame() + partialTick;
            float speed = 0.2F;
            float amplitude = 0.5F;
            float phase = time * speed;
            poseStack.translate(0F, Mth.sin(phase) * amplitude, 0F);
        }
    }
}
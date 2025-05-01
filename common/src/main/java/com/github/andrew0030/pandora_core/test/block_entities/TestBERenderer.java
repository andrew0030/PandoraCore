package com.github.andrew0030.pandora_core.test.block_entities;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.github.andrew0030.pandora_core.client.registry.PaCoModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TestBERenderer implements BlockEntityRenderer<TestBlockEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/entity/test.png");
    private final TestBEModel model;

    public TestBERenderer(BlockEntityRendererProvider.Context context) {
        this.model = new TestBEModel(context.bakeLayer(PaCoModelLayers.TEST));
    }

    @Override
    public void render(TestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}

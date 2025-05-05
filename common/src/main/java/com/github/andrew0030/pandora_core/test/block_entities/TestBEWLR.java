package com.github.andrew0030.pandora_core.test.block_entities;

import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.github.andrew0030.pandora_core.registry.PaCoBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TestBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final TestBEWLR INSTANCE = new TestBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    private final TestBlockEntity test = new TestBlockEntity(BlockPos.ZERO, PaCoBlocks.TEST.get().defaultBlockState());
    private final BlockEntityRenderDispatcher renderDispatcher;

    public TestBEWLR(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
        this.renderDispatcher = renderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item == PaCoBlocks.TEST.get().asItem())
            this.renderDispatcher.renderItem(this.test, poseStack, buffer, packedLight, packedOverlay);
    }
}
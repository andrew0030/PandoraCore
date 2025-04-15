package com.github.andrew0030.pandora_core.client.render.renderers.registry;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class InstancedBERendererRegistry {
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, InstancedBlockEntityRenderer<T> renderer) {
        ((BlockEntityTypeAttachments) type).pandoraCore$setInstancedRenderer(renderer);
    }

    public static <T extends BlockEntity> InstancedBlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        //noinspection unchecked
        return (InstancedBlockEntityRenderer<T>) ((BlockEntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }
}

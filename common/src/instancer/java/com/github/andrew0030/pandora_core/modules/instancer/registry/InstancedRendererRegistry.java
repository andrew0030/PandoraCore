package com.github.andrew0030.pandora_core.modules.instancer.registry;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class InstancedRendererRegistry {
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, InstancedBlockEntityRenderer<T> renderer) {
        ((BlockEntityTypeAttachments) type).pandoraCore$setInstancedRenderer(renderer);
    }

    public static <T extends BlockEntity> InstancedBlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        //noinspection unchecked
        return (InstancedBlockEntityRenderer<T>) ((BlockEntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }
	
    public static <T extends Entity> void register(EntityType<T> type, InstancedEntityRenderer<T> renderer) {
        ((EntityTypeAttachments) type).pandoraCore$setInstancedRenderer(renderer);
    }

    public static <T extends Entity> InstancedEntityRenderer<T> getRenderer(EntityType<T> type) {
        //noinspection unchecked
        return (InstancedEntityRenderer<T>) ((EntityTypeAttachments) type).pandoraCore$getInstancedRenderer();
    }
	
    public static void register(Item type, InstancedItemRenderer renderer) {
        ((ItemAttachments) type).pandoraCore$setInstancedRenderer(renderer);
    }

    public static InstancedItemRenderer getRenderer(Item type) {
        //noinspection unchecked
        return ((ItemAttachments) type).pandoraCore$getInstancedRenderer();
    }
}

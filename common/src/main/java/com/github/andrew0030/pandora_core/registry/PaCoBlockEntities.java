package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.block_entities.InstancingTestBlockEntity;
import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.github.andrew0030.pandora_core.test.block_entities.TestBERenderer;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class PaCoBlockEntities {
    public static final PaCoRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new PaCoRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, PandoraCore.MOD_ID);

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST                      = BLOCK_ENTITY_TYPES.add("test", () -> new BlockEntityType<>(TestBlockEntity::new, Sets.newHashSet(PaCoBlocks.TEST.get()), null));
    public static final Supplier<BlockEntityType<InstancingTestBlockEntity>> INSTANCING_TEST = BLOCK_ENTITY_TYPES.add("instancing_test", () -> new BlockEntityType<>(InstancingTestBlockEntity::new, Sets.newHashSet(PaCoBlocks.INSTANCING_TEST.get()), null));

    public static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(TEST.get(), TestBERenderer::new);
    }
}
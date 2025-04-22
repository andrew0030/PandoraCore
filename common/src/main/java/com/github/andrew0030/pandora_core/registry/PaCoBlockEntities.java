package com.github.andrew0030.pandora_core.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.block_entities.TestBlockEntity;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class PaCoBlockEntities {
    public static final PaCoRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new PaCoRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, PandoraCore.MOD_ID);

    public static final Supplier<BlockEntityType<?>> TEST = BLOCK_ENTITY_TYPES.add("test", () -> new BlockEntityType<>(TestBlockEntity::new, Sets.newHashSet(PaCoBlocks.TEST.get()), null));
}
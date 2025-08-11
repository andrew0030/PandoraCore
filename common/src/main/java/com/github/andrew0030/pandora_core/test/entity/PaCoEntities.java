package com.github.andrew0030.pandora_core.test.entity;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.registry.PaCoRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class PaCoEntities {
    public static final PaCoRegistry<EntityType<?>> ENTITY_TYPES = new PaCoRegistry<>(BuiltInRegistries.ENTITY_TYPE, PandoraCore.MOD_ID);

    public static final Supplier<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPES.add("test_entity", () -> EntityType.Builder.of(TestEntity::new, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10).build(new ResourceLocation(PandoraCore.MOD_ID, "test_entity").toString()));

    public static void registerEntityRenderers() {
        EntityRenderers.register(TEST_ENTITY.get(), TestEntityRenderer::new);
    }
}
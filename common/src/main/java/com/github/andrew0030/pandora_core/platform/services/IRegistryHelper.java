package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.client.registry.PaCoParticleProviderRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoBrewingRecipeRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryBuilder;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue);

    <T> void registerCustom(PaCoRegistryBuilder.SimpleSpec<T> spec, String modId, Map<String, PaCoRegistryObject<T>> registryQueue);

    <T> void registerDynamic(PaCoRegistryBuilder.DynamicSpec<T> spec);

    void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings);

    void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers);

    void registerFlammableBlocks(Map<Block, PaCoFlammableBlockRegistry.Entry> flammables);

    void registerColorHandlers(Map<Supplier<Block>, BlockColor> blockColors, Map<Supplier<? extends ItemLike>, ItemColor> itemColors);

    void registerBlockRenderTypes(Map<Supplier<Block>, RenderType> renderTypes);

    void registerParticleProviders(Map<ParticleType<?>, ParticleProvider<?>> particleProviders, Map<ParticleType<?>, PaCoParticleProviderRegistry.PendingParticleProvider<?>> pendingParticleProviders);

    void registerBrewingRecipes(List<PaCoBrewingRecipeRegistry.Entry> brewingRecipes);

    void registerEntityAttributes(Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> entityAttributes);
}
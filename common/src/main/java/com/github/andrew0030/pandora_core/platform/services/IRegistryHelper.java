package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue);

    void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings);

    void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers);

    void registerFlammableBlocks(Map<Block, PaCoFlammableBlockRegistry.Entry> flammables);

    void registerColorHandlers(Map<Supplier<Block>, BlockColor> blockColors, Map<Supplier<? extends ItemLike>, ItemColor> itemColors);
}
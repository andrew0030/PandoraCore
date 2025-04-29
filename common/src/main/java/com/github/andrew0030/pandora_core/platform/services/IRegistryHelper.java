package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue);

    void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings);

    void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers);
}
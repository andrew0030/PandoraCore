package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IRegistryHelper;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue) {
        registryQueue.forEach((name, registryObject) -> {
            Registry.register(registry, new ResourceLocation(modId, name), registryObject.get());
        });
    }

    @Override
    public void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings) {
        // Registers Key Mappings.
        for (Pair<KeyMapping, Runnable> pair : keyMappings) {
            KeyBindingHelper.registerKeyBinding(pair.getFirst());
        }
        // Handle inputs for all Key Mappings.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                while (pair.getFirst().consumeClick()) {
                    pair.getSecond().run();
                }
            }
        });
    }

    @Override
    public void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers) {
        // Registers Model Layers.
        modelLayers.forEach((location, definition) -> EntityModelLayerRegistry.registerModelLayer(location, definition::get));
    }
}
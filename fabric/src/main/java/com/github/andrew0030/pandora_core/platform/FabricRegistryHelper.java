package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.client.registry.PaCoParticleProviderRegistry;
import com.github.andrew0030.pandora_core.platform.services.IRegistryHelper;
import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

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

    @Override
    public void registerFlammableBlocks(Map<Block, PaCoFlammableBlockRegistry.Entry> flammables) {
        for (Map.Entry<Block, PaCoFlammableBlockRegistry.Entry> entry : flammables.entrySet()) {
            Block block = entry.getKey();
            int igniteOdds = entry.getValue().igniteOdds();
            int burnOdds = entry.getValue().burnOdds();
            FlammableBlockRegistry.getDefaultInstance().add(block, igniteOdds, burnOdds);
        }
    }

    @Override
    public void registerColorHandlers(Map<Supplier<Block>, BlockColor> blockColors, Map<Supplier<? extends ItemLike>, ItemColor> itemColors) {
        // Registers Block Color Handlers
        for (Map.Entry<Supplier<Block>, BlockColor> entry : blockColors.entrySet())
            ColorProviderRegistry.BLOCK.register(entry.getValue(), entry.getKey().get());
        // Registers ItemLike Color Handlers
        for (Map.Entry<Supplier<? extends ItemLike>, ItemColor> entry : itemColors.entrySet())
            ColorProviderRegistry.ITEM.register(entry.getValue(), entry.getKey().get());
    }

    @Override
    public void registerBlockRenderTypes(Map<Supplier<Block>, RenderType> renderTypes) {
        // Registers Block Render Types.
        for (Map.Entry<Supplier<Block>, RenderType> entry : renderTypes.entrySet())
            BlockRenderLayerMap.INSTANCE.putBlock(entry.getKey().get(), entry.getValue());
    }

    @Override
    public void registerParticleProviders(Map<ParticleType<?>, ParticleProvider<?>> particleProviders, Map<ParticleType<?>, PaCoParticleProviderRegistry.PendingParticleProvider<?>> pendingParticleProviders) {
        // Registers Particle Providers.
        for (Map.Entry<ParticleType<?>, ParticleProvider<?>> entry : particleProviders.entrySet())
            FabricRegistryHelper.registerParticleProvider(entry.getKey(), entry.getValue());
        for (Map.Entry<ParticleType<?>, PaCoParticleProviderRegistry.PendingParticleProvider<?>> entry : pendingParticleProviders.entrySet())
            FabricRegistryHelper.registerPendingParticleProvider(entry.getKey(), entry.getValue());
    }

    @SuppressWarnings("unchecked")
    private static <T extends ParticleOptions> void registerParticleProvider(ParticleType<?> type, ParticleProvider<?> provider) {
        ParticleFactoryRegistry.getInstance().register((ParticleType<T>) type, (ParticleProvider<T>) provider);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ParticleOptions> void registerPendingParticleProvider(ParticleType<?> type, PaCoParticleProviderRegistry.PendingParticleProvider<?> pendingProvider) {
        ParticleFactoryRegistry.getInstance().register((ParticleType<T>) type, spriteSet -> (ParticleProvider<T>) pendingProvider.create(spriteSet));
    }
}
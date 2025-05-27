package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IRegistryHelper;
import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;
import com.github.andrew0030.pandora_core.registry.PaCoRegistryObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public <T> void register(Registry<T> registry, String modId, Map<String, PaCoRegistryObject<T>> registryQueue) {
        DeferredRegister<T> deferred = DeferredRegister.create(registry.key(), modId);
        registryQueue.forEach(deferred::register);
        deferred.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    public void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Registers Key Mappings.
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                event.register(pair.getFirst());
            }
        });
        // Handle inputs for all Key Mappings.
        forgeEventBus.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                    while (pair.getFirst().consumeClick()) {
                        pair.getSecond().run();
                    }
                }
            }
        });
    }

    @Override
    public void registerModelLayers(Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Model Layers.
        modEventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
            modelLayers.forEach(event::registerLayerDefinition);
        });
    }

    @Override
    public void registerFlammableBlocks(Map<Block, PaCoFlammableBlockRegistry.Entry> flammables) {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (Map.Entry<Block, PaCoFlammableBlockRegistry.Entry> entry : flammables.entrySet()) {
            Block block = entry.getKey();
            int igniteOdds = entry.getValue().igniteOdds();
            int burnOdds = entry.getValue().burnOdds();
            fireblock.setFlammable(block, igniteOdds, burnOdds);
        }
    }

    @Override
    public void registerColorHandlers(Map<Supplier<Block>, BlockColor> blockColors, Map<Supplier<? extends ItemLike>, ItemColor> itemColors) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Block Color Handlers
        modEventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
            for (Map.Entry<Supplier<Block>, BlockColor> entry : blockColors.entrySet())
                event.register(entry.getValue(), entry.getKey().get());
        });
        // Registers ItemLike Color Handlers
        modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
            for (Map.Entry<Supplier<? extends ItemLike>, ItemColor> entry : itemColors.entrySet())
                event.register(entry.getValue(), entry.getKey().get());
        });
    }

    @Override
    public void registerBlockRenderTypes(Map<Supplier<Block>, RenderType> renderTypes) {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registers Block Render Types.
        modEventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            for (Map.Entry<Supplier<Block>, RenderType> entry : renderTypes.entrySet())
                ItemBlockRenderTypes.setRenderLayer(entry.getKey().get(), entry.getValue());
        });
    }
}
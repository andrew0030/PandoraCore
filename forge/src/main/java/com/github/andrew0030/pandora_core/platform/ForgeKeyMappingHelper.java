package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IKeyMappingHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

public class ForgeKeyMappingHelper implements IKeyMappingHelper {

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
}
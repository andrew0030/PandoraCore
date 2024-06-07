package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.events.ForgeClientTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PandoraCoreClientForge {

    public static void init(IEventBus modEventBus, IEventBus forgeEventBus)
    {
        modEventBus.addListener(PandoraCoreClientForge::clientSetup);
        modEventBus.addListener(PandoraCoreClientForge::registerKeyMappings);

        forgeEventBus.addListener(PandoraCoreClientForge::handleKeyMappings);
        forgeEventBus.addListener(ForgeClientTickEvent::init);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        // Common Module Initialization.
        PandoraCoreClient.init();
        event.enqueueWork(PandoraCoreClient::initThreadSafe);

        // Loader Module Initialization.
        // Nothing atm...
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : PaCoKeyMappings.KEY_MAPPINGS) {
            event.register(keyMapping);
        }
    }

    private static void handleKeyMappings(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (PaCoKeyMappings.KEY_PACO.consumeClick()) {
                Minecraft.getInstance().setScreen(new PaCoScreen());
            }
        }
    }
}
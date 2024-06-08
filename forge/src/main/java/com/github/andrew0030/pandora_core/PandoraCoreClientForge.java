package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.key.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.events.ForgeClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PandoraCoreClientForge {

    public static void init(IEventBus modEventBus, IEventBus forgeEventBus)
    {
        // Mod Event Bus
        modEventBus.addListener(PandoraCoreClientForge::clientSetup);
        // Forge Event Bus
        forgeEventBus.addListener(ForgeClientTickEvent::init);

        // Registers the PaCo KeyMappings
        PaCoKeyMappings.KEY_MAPPINGS.registerKeyBindings();
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        // Common Module Initialization.
        PandoraCoreClient.init();
        event.enqueueWork(PandoraCoreClient::initThreadSafe);

        // Loader Module Initialization.
        // Nothing atm...
    }
}
package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.registry.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.events.FabricClientTickEvent;
import net.fabricmc.api.ClientModInitializer;

public class PandoraCoreClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Common Module Initialization.
        PandoraCoreClient.earlyInit();
        PandoraCoreClient.init();
        PandoraCoreClient.initThreadSafe();

        // Loader Module Initialization.
        FabricClientTickEvent.init();

        // Registers the PaCo KeyMappings
        PaCoKeyMappings.KEY_MAPPINGS.registerKeyBindings();
    }
}
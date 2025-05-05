package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.events.FabricClientTickEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class PandoraCoreClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Common Module Initialization.
        PandoraCoreClient.earlyInit();
        PandoraCoreClient.init();
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraft) -> PandoraCoreClient.initThreadSafe());

        // Loader Module Initialization.
        FabricClientTickEvent.init();
    }
}
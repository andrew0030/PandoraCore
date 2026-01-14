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

        //TODO: The "initThreadSafe" method was moved into the "CLIENT_STARTED" event to ensure the shader patcher works.
        // That said since BEWLRs no longer require "initThreadSafe" and instead work with "init", thus the render related stuff
        // should be separated, and kept inside "CLIENT_STARTED", while "initThreadSafe" should return to being called directly.
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraft) -> PandoraCoreClient.initThreadSafe());

        // Loader Module Initialization.
        FabricClientTickEvent.init();
    }
}
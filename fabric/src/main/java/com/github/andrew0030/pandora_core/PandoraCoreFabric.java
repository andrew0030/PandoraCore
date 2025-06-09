package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.events.FabricServerLifecycleEvents;
import net.fabricmc.api.ModInitializer;

public class PandoraCoreFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // Common Module Initialization.
        PandoraCore.earlyInit();
        PandoraCore.init();
        PandoraCore.initThreadSafe();

        // Loader Module Initialization.
        FabricServerLifecycleEvents.init();
    }
}
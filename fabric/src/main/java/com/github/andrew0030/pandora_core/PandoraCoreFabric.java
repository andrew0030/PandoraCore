package com.github.andrew0030.pandora_core;

import net.fabricmc.api.ModInitializer;

public class PandoraCoreFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // Common Module Initialization.
        PandoraCore.init();
        PandoraCore.initThreadSafe();

        // Loader Module Initialization.
        // Nothing atm...
    }
}
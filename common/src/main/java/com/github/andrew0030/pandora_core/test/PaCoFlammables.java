package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.registry.test.PaCoBlocks;
import com.github.andrew0030.pandora_core.registry.PaCoFlammableBlockRegistry;

public class PaCoFlammables {
    public static final PaCoFlammableBlockRegistry FLAMMABLES = new PaCoFlammableBlockRegistry();

    static {
        FLAMMABLES.add(PaCoBlocks.CONNECTED_BLOCK.get(), 30, 60);
    }
}
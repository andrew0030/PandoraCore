package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.platform.services.IKeyMappingHelper;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import java.util.List;

public class FabricKeyMappingHelper implements IKeyMappingHelper {

    @Override
    public void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings) {
        // Registers Key Mappings.
        for (Pair<KeyMapping, Runnable> pair : keyMappings) {
            KeyBindingHelper.registerKeyBinding(pair.getFirst());
        }
        // Handle inputs for all Key Mappings.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Pair<KeyMapping, Runnable> pair : keyMappings) {
                while (pair.getFirst().consumeClick()) {
                    pair.getSecond().run();
                }
            }
        });
    }
}
package com.github.andrew0030.pandora_core.events;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class FabricClientTickEvent {
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                if (!mc.isPaused()) {
                    PaCoClientTicker.tickGame(); // Updates the game client tick counter
                    ScreenShakeManager.tickCameraShakes();
                }
            }
            PaCoClientTicker.tickGlobal(); // Updates the global client tick counter
        });
    }
}
package com.github.andrew0030.pandora_core.events;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.screen_shaker.ScreenShakeManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;

public class ForgeClientTickEvent {
    public static void init(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                if (!mc.isPaused()) {
                    PaCoClientTicker.tickGame(); // Updates the game client tick counter
                    ScreenShakeManager.tickCameraShakes();
                }
            }
            PaCoClientTicker.tickGlobal(); // Updates the global client tick counter
        }
    }
}
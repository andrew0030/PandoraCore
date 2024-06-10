package com.github.andrew0030.pandora_core.client;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

public class PaCoClientTicker {
    public static int globalTickCount = 0;
    public static int gameTickCount = 0;

    /** @return Total client tick count. */
    public static int getGlobal() {
        return PaCoClientTicker.globalTickCount;
    }

    /** @return Total game tick count. This counter doesn't tick when there is no level, or if the game is paused. */
    public static int getGame() {
        return PaCoClientTicker.gameTickCount;
    }

    /** @return The current partialTick. */
    public static float getPartialTick() {
        Minecraft mc = Minecraft.getInstance();
        return mc.isPaused() ? mc.pausePartialTick : mc.getFrameTime();
    }

    @ApiStatus.Internal
    public static void tickGlobal() {
        PaCoClientTicker.globalTickCount++;
    }

    @ApiStatus.Internal
    public static void tickGame() {
        PaCoClientTicker.gameTickCount++;
    }
}
package com.github.andrew0030.pandora_core.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

public class FabricServerLifecycleEvents {
    private static MinecraftServer server;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(srv -> server = srv);
        ServerLifecycleEvents.SERVER_STOPPED.register(srv -> server = null);
    }

    public static @Nullable MinecraftServer getServer() {
        return FabricServerLifecycleEvents.server;
    }
}
package com.github.andrew0030.pandora_core.test.networking;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.network.PaCoPacketRegistry;
import com.github.andrew0030.pandora_core.test.networking.packet.clientbound.OpenTestGUIPacket;
import com.github.andrew0030.pandora_core.test.networking.packet.serverbound.KeyPressedPacket;
import net.minecraft.resources.ResourceLocation;

public class PaCoNetworking {
    public static final PaCoPacketRegistry CHANNEL = PaCoPacketRegistry.of(new ResourceLocation(PandoraCore.MOD_ID, "main"));

    static {
        // Server to Client
        CHANNEL.add(OpenTestGUIPacket.TYPE);
        // Client to Server
        CHANNEL.add(KeyPressedPacket.TYPE);
    }
}
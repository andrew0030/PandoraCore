package com.github.andrew0030.pandora_core.test.networking;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.network.PaCoPacketChannel;
import com.github.andrew0030.pandora_core.test.networking.packet.c2s.KeyPressedPacket;
import com.github.andrew0030.pandora_core.test.networking.packet.s2c.OpenTestGUIPacket;
import net.minecraft.resources.ResourceLocation;

public class PaCoNetworking {
    public static final String NETWORK_VERSION = "1.0.0";
    public static final PaCoPacketChannel CHANNEL = PaCoPacketChannel.of(
            new ResourceLocation(PandoraCore.MOD_ID, "main"),
            NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    static {
        // Server to Client
        PaCoPacketChannel.add(CHANNEL, OpenTestGUIPacket.class, OpenTestGUIPacket::new);
        // Client to Server
        PaCoPacketChannel.add(CHANNEL, KeyPressedPacket.class, KeyPressedPacket::new);
    }
}
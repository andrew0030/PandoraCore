package com.github.andrew0030.pandora_core.test.networking;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.network.NetworkEntry;
import com.github.andrew0030.pandora_core.network.PacketRegister;
import com.github.andrew0030.pandora_core.test.networking.packet.c2s.KeyPressedPacket;
import com.github.andrew0030.pandora_core.test.networking.packet.s2c.OpenTestGUIPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PaCoNetworking {
    public static final String NETWORK_VERSION = "1.0.0";
    public static final PacketRegister NETWORK_INSTANCE = PacketRegister.of(
            new ResourceLocation(PandoraCore.MOD_ID, "main"),
            NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    static {
        List<NetworkEntry<?>> entries = new ArrayList<>();
        // Server to Client
        entries.add(new NetworkEntry<>(OpenTestGUIPacket.class, OpenTestGUIPacket::new));
        // Client to Server
        entries.add(new NetworkEntry<>(KeyPressedPacket.class, KeyPressedPacket::new));

        for (int i = 0; i < entries.size(); i++)
            entries.get(i).register(i, NETWORK_INSTANCE);
    }

    public static void init() {}
}
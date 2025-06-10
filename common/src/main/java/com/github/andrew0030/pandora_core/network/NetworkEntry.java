package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class NetworkEntry<T extends PaCoPacket> {
    private final Class<T> clazz;
    private final Function<FriendlyByteBuf, T> fabricator;

    public NetworkEntry(Class<T> clazz, Function<FriendlyByteBuf, T> fabricator) {
        this.clazz = clazz;
        this.fabricator = fabricator;
    }

    public void register(int index, PacketRegister channel) {
        channel.registerMessage(
                index, this.clazz,
                PaCoPacket::write,
                this.fabricator,
                PaCoPacket::handle
        );
    }
}
package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PaCoPacketType<T extends PaCoPacket> {
    public final Class<T> packetClass;
    public final PacketFlow flow;
    public final ResourceLocation location;
    public final BiConsumer<T, FriendlyByteBuf> writer;
    public final Function<FriendlyByteBuf, T> reader;
    public final PayloadHandler<T> handler;

    public PaCoPacketType(
            Class<T> packetClass, PacketFlow flow, ResourceLocation location,
            BiConsumer<T, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> reader,
            PayloadHandler<T> handler
    ) {
        this.packetClass = packetClass;
        this.flow = flow;
        this.location = location;
        this.writer = writer;
        this.reader = reader;
        this.handler = handler;
    }
}
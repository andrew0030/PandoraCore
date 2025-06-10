package com.github.andrew0030.pandora_core.network;

import java.util.function.Consumer;

public class PacketSender {
    PacketRegister registry;
    Consumer<PaCoPacket> sender;

    public PacketSender(PacketRegister registry, Consumer<PaCoPacket> sender) {
        this.registry = registry;
        this.sender = sender;
    }

    public void send(PaCoPacket packet) {
        sender.accept(packet);
    }
}
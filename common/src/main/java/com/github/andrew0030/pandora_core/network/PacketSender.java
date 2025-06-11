package com.github.andrew0030.pandora_core.network;

import java.util.function.Consumer;

/**
 * Utility class for sending {@link PaCoPacket} instances using a predefined sending logic.
 */
public class PacketSender {
    private final PaCoPacketChannel channel;
    private final Consumer<PaCoPacket> sender;

    /**
     * Constructs a new {@link  PacketSender}.
     *
     * @param channel the {@link PaCoPacketChannel} used for encoding and dispatching packets
     * @param sender  a {@link Consumer} that defines how the packet will be sent
     */
    public PacketSender(PaCoPacketChannel channel, Consumer<PaCoPacket> sender) {
        this.channel = channel;
        this.sender = sender;
    }

    /**
     * Sends the provided {@link PaCoPacket} using the configured sending logic.
     *
     * @param packet the {@link PaCoPacket} to send
     */
    public void send(PaCoPacket packet) {
        sender.accept(packet);
    }

    /**
     * Returns the {@link PaCoPacketChannel} associated with this sender.
     *
     * @return the associated {@link PaCoPacketChannel}
     */
    public PaCoPacketChannel getChannel() {
        return this.channel;
    }
}
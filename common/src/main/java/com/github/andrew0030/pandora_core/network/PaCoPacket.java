package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Represents a network packet within the PaCo networking system.
 * <p>
 * Subclasses of this abstract class define custom packets for
 * sending data between the {@code client} and {@code server}.
 * <p>
 * To create a custom packet, extend this class and implement the
 * {@link #write(FriendlyByteBuf)} and {@link #handle(NetCtx)} methods.
 * You must also provide a constructor that accepts a {@link FriendlyByteBuf}
 * for deserializing packet data.
 */
public abstract class PaCoPacket {

    /**
     * Constructs a new packet instance for sending.
     */
    public PaCoPacket() {}

    /**
     * Constructs a new packet by reading data from the provided {@link FriendlyByteBuf}.
     * <p>
     * This constructor should be implemented by subclasses to decode the packet's data.
     * The order in which data is read from the buffer must exactly match the order
     * it was written in {@link #write(FriendlyByteBuf)} to avoid mismatches.
     * <p>
     * Example usage:
     * <pre>{@code
     *     this.value = buf.readInt();
     *     this.text = buf.readUtf();
     * }</pre>
     *
     * @param buf the buffer containing serialized packet data received from the network
     */
    public PaCoPacket(FriendlyByteBuf buf) {}

    /**
     * Serializes the packet data into the provided {@link FriendlyByteBuf}.
     *
     * @param buf the buffer to write the packet's data into
     */
    public abstract void write(FriendlyByteBuf buf);

    /**
     * Handles the logic for this packet when it is received.
     *
     * @param context the {@link NetCtx} of the packet
     */
    public abstract void handle(NetCtx context);

    /**
     * Sends a response {@link PaCoPacket} back to the origin of the current network context.
     * <p>
     * This is a convenience wrapper for {@code context.respond(packet)}.
     *
     * @param context the {@link NetCtx} representing the current packet context.
     * @param packet  the {@link PaCoPacket} to send as a response.
     *
     * @see NetCtx#respond(PaCoPacket)
     */
    public void respond(NetCtx context, PaCoPacket packet) {
        context.respond(packet);
    }

    /**
     * Checks whether the current network context is running on the client side.
     * <p>
     * This is a convenience wrapper for {@code context.checkClient()}.
     *
     * @param context the {@link NetCtx} representing the current packet context.
     * @return {@code true} if the packet is being handled on the client side.
     *
     * @see NetCtx#checkClient()
     */
    public boolean checkClient(NetCtx context) {
        return context.checkClient();
    }

    /**
     * Checks whether the current network context is running on the server side.
     * <p>
     * This is a convenience wrapper for {@code context.checkServer()}.
     *
     * @param context the {@link NetCtx} representing the current packet context.
     * @return {@code true} if the packet is being handled on the server side.
     *
     * @see NetCtx#checkServer()
     */
    public boolean checkServer(NetCtx context) {
        return context.checkServer();
    }
}
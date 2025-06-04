package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.FriendlyByteBuf;

public abstract class Packet {

    public Packet() {}

    public Packet(FriendlyByteBuf buf) {}

    public abstract void write(FriendlyByteBuf buf);

    public abstract void handle(NetCtx context);

    /**
     * Sends a response {@link Packet} back to the origin of the current network context.
     * <p>
     * This is a convenience wrapper for {@code context.respond(packet)}.
     *
     * @param context the {@link NetCtx} representing the current packet context.
     * @param packet  the {@link Packet} to send as a response.
     *
     * @see NetCtx#respond(Packet)
     */
    public void respond(NetCtx context, Packet packet) {
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
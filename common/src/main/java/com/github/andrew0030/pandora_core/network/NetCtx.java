package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public abstract class NetCtx {
    protected final PacketListener handler;
    protected final PacketSender responseSender;
    protected final ServerPlayer sender;
    protected final NetworkDirection direction;

    public NetCtx(PacketListener handler, PacketSender responseSender, ServerPlayer player, NetworkDirection direction) {
        this.handler = handler;
        this.responseSender = responseSender;
        this.sender = player;
        this.direction = direction;
    }

    /**
     * @return the current handler for processing packets
     */
    public PacketListener getHandler() {
        return this.handler;
    }

    /**
     * Sends a response {@link PaCoPacket} back to the origin of the current network context.
     *
     * @param packet  the {@link PaCoPacket} to send as a response.
     */
    public void respond(PaCoPacket packet) {
        this.responseSender.send(packet);
    }

    /**
     * When available, gets the sender for packets that are sent from a client to the server.
     */
    public @Nullable ServerPlayer getSender() {
        return this.sender;
    }

    public NetworkDirection getDirection() {
        return this.direction;
    }

    /**
     * Defers execution of the given {@link Runnable} to the main game thread.
     * <p>
     * This is important for thread safety, as network handlers may run on a separate thread.<br/>
     * Use this method to schedule logic that must interact with game states (e.g., world or entities).
     *
     * @param runnable the task to execute
     */
    public abstract void enqueueWork(Runnable runnable);

    /**
     * Sets whether the packet was successfully handled.
     *
     * @param packetHandled {@code true} if the packet was handled successfully
     */
    public void setPacketHandled(boolean packetHandled) {}

    /**
     * Checks whether the current network context is running on the client side.
     *
     * @return {@code true} if the packet is being handled on the client side.
     */
    public boolean checkClient() {
        return this.getDirection().equals(NetworkDirection.TO_CLIENT);
    }

    /**
     * Checks whether the current network context is running on the server side.
     *
     * @return {@code true} if the packet is being handled on the server side.
     */
    public boolean checkServer() {
        return this.getDirection().equals(NetworkDirection.TO_SERVER);
    }
}
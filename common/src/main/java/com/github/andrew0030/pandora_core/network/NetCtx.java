package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

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

    public PacketListener getHandler() {
        return this.handler;
    }

    public void respond(Packet packet) {
        this.responseSender.send(packet);
    }

    public ServerPlayer getSender() {
        return this.sender;
    }

    public NetworkDirection getDirection() {
        return this.direction;
    }

    public abstract void enqueueWork(Runnable runnable);

    public void setPacketHandled(boolean b) {}

    public boolean checkClient() {
        return this.direction.equals(NetworkDirection.TO_CLIENT);
    }

    public boolean checkServer() {
        return this.direction.equals(NetworkDirection.TO_SERVER);
    }
}
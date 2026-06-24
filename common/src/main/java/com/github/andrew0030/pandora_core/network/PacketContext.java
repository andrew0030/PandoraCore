package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class PacketContext {
    public final PacketListener listener;
    public final ServerPlayer sender;
    public final PacketFlow flow;
    public final Consumer<Runnable> scheduler;
    private final Consumer<PaCoPacket> replyAction;

    public PacketContext(PacketListener listener, ServerPlayer sender, PacketFlow flow, Consumer<Runnable> scheduler, Consumer<PaCoPacket> replyAction) {
        this.listener = listener;
        this.sender = sender;
        this.flow = flow;
        this.scheduler = scheduler;
        this.replyAction = replyAction;
    }

    public void reply(PaCoPacket packet) {
        this.replyAction.accept(packet);
    }

    public void enqueue(Runnable r) {
        this.scheduler.accept(r);
    }
}
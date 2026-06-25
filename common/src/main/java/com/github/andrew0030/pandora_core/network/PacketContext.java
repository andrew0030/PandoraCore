package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class PacketContext {
    private final PacketListener listener;
    private final ServerPlayer sender;
    private final PaCoPacketFlow flow;
    private final Consumer<Runnable> scheduler;
    private final Consumer<PaCoPacket> replyAction;

    public PacketContext(PacketListener listener, ServerPlayer sender, PaCoPacketFlow flow, Consumer<Runnable> scheduler, Consumer<PaCoPacket> replyAction) {
        this.listener = listener;
        this.sender = sender;
        this.flow = flow;
        this.scheduler = scheduler;
        this.replyAction = replyAction;
    }

    /** @return The current {@link PacketListener} for processing packets. */
    public PacketListener getPacketListener() {
        return this.listener;
    }

    /** When available, gets the sender for packets that are sent from a client to the server. */
    public ServerPlayer getSender() {
        return this.sender;
    }

    /** @return The {@link PaCoPacketFlow} representing the actual direction the current packet traveled. */
    public PaCoPacketFlow getFlow() {
        return this.flow;
    }

    /**
     * Defers execution of the given {@link Runnable} to the main game thread.
     * <p>This is important for thread safety, as network handlers may run on a separate thread.</p>
     * Use this method to schedule logic that must interact with game states (e.g., world or entities).
     *
     * @param runnable the task to execute
     */
    public void enqueue(Runnable runnable) {
        this.scheduler.accept(runnable);
    }

    /**
     * Sends a response {@link PaCoPacket} back to the origin of the current network context.
     *
     * @param packet  the {@link PaCoPacket} to send as a response.
     */
    public void reply(PaCoPacket packet) {
        this.replyAction.accept(packet);
    }
}
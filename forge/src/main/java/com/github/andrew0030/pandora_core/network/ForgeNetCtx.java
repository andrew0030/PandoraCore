package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ForgeNetCtx extends NetCtx {
    private final NetworkEvent.Context context;

    public ForgeNetCtx(PacketListener handler, PacketSender responseSender, ServerPlayer player, NetworkDirection direction, NetworkEvent.Context context) {
        super(handler, responseSender, player, direction);
        this.context = context;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        this.context.enqueueWork(runnable);
    }

    @Override
    public void setPacketHandled(boolean packetHandled) {
        this.context.setPacketHandled(packetHandled);
    }
}
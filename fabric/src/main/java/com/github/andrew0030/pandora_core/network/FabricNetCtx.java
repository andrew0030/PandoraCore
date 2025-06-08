package com.github.andrew0030.pandora_core.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetCtx extends NetCtx {

    public FabricNetCtx(PacketListener handler, PacketSender responseSender, ServerPlayer player, NetworkDirection direction) {
        super(handler, responseSender, player, direction);
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && (sender == null || sender.level().isClientSide)) {
            Minecraft.getInstance().tell(runnable);
        } else {
            if (sender != null) {
                sender.getServer().execute(runnable);
            } else {
                runnable.run(); // whar
//                Loggers.SU_LOGGER.warn("A null sender on server???");
            }
        }
    }

    @Override
    public void setPacketHandled(boolean b) {}
}
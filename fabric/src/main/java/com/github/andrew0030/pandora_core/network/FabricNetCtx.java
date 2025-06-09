package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class FabricNetCtx extends NetCtx {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "FabricNetCtx");

    public FabricNetCtx(PacketListener handler, PacketSender responseSender, ServerPlayer player, NetworkDirection direction) {
        super(handler, responseSender, player, direction);
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && (sender == null || sender.level().isClientSide)) {
            Minecraft.getInstance().tell(runnable);
        } else {
            if (sender != null && sender.getServer() != null) {
                sender.getServer().execute(runnable);
            } else {
                runnable.run(); // whar
                LOGGER.warn("A null sender or server on server???");
            }
        }
    }
}
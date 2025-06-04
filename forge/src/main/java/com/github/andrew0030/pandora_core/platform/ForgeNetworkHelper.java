package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.network.ForgePacketRegister;
import com.github.andrew0030.pandora_core.network.PacketRegister;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import com.github.andrew0030.pandora_core.platform.services.INetworkHelper;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Predicate;

public class ForgeNetworkHelper implements INetworkHelper {

    @Override
    public PacketRegister getPacketRegistry(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        return new ForgePacketRegister(name, networkVersion, clientChecker, serverChecker);
    }

    @Override
    public PacketTarget sendToServer() {
        return new PacketTarget((packet, packetRegister) -> {
            ((ForgePacketRegister) packetRegister).channel.sendToServer(packet);
        });
    }

    @Override
    public PacketTarget sendToPlayer(ServerPlayer player) {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
        });
    }

    @Override
    public PacketTarget sendToDimension(ResourceKey<Level> dimension) {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
        });
    }

    @Override
    public PacketTarget sendToAll() {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.ALL.noArg(), packet);
        });
    }

    @Override
    public PacketTarget sendToTrackingEntity(Entity entity) {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
        });
    }

    @Override
    public PacketTarget sendToTrackingEntityAndSelf(Entity entity) {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
        });
    }

    @Override
    public PacketTarget sendToTrackingChunk(LevelChunk chunk) {
        return new PacketTarget((packet, register) -> {
            ((ForgePacketRegister) register).channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
        });
    }
}
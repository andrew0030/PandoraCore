package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.network.FabricPacketRegister;
import com.github.andrew0030.pandora_core.network.PacketRegister;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import com.github.andrew0030.pandora_core.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;

public class FabricNetworkHelper implements INetworkHelper {

    @Override
    public PacketRegister getPacketRegistry(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        return new FabricPacketRegister(name, networkVersion, clientChecker, serverChecker);
    }

    @Override
    public PacketTarget sendToServer() {
        return new PacketTarget((packet, register) -> {
            ClientPlayNetworking.send(register.channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToPlayer(ServerPlayer player) {
        return new PacketTarget((packet, register) -> {
            ServerPlayNetworking.send(player, register.channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToDimension(ResourceKey<Level> dimension) {
        return null;
    }

    @Override
    public PacketTarget sendToAll() {
        return null;
    }

    @Override
    public PacketTarget sendToTrackingEntity(Entity entity) {
        return new PacketTarget((packet, register) -> {
            PlayerLookup.tracking(entity).forEach(
                    player -> ServerPlayNetworking.send(player, register.channel, register.encode(packet))
            );
        });
    }

    @Override
    public PacketTarget sendToTrackingEntityAndSelf(Entity entity) {
        return new PacketTarget((packet, register) -> {
            PlayerLookup.tracking(entity).forEach(
                    player -> ServerPlayNetworking.send(player, register.channel, register.encode(packet))
            );
            // Sends to the entity itself, if it's a ServerPlayer
            if (entity instanceof ServerPlayer player)
                ServerPlayNetworking.send(player, register.channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToTrackingChunk(LevelChunk chunk) {
        return new PacketTarget((packet, register) -> {
            ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(
                    player -> ServerPlayNetworking.send(player, register.channel, register.encode(packet))
            );
        });
    }
}
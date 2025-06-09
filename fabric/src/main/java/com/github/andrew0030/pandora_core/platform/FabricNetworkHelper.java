package com.github.andrew0030.pandora_core.platform;

import com.github.andrew0030.pandora_core.events.FabricServerLifecycleEvents;
import com.github.andrew0030.pandora_core.network.FabricPacketRegister;
import com.github.andrew0030.pandora_core.network.PacketRegister;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import com.github.andrew0030.pandora_core.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
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
            ClientPlayNetworking.send(((FabricPacketRegister) register).channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToPlayer(ServerPlayer player) {
        return new PacketTarget((packet, register) -> {
            ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToDimension(ResourceKey<Level> dimension) {
        return new PacketTarget((packet, register) -> {
            MinecraftServer server = FabricServerLifecycleEvents.getServer();
            if (server != null) {
                ServerLevel level = server.getLevel(dimension);
                if (level != null) {
                    level.players().forEach(
                            player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
                    );
                }
            } else {
                System.out.println("No Server!");
            }
        });
    }

    @Override
    public PacketTarget sendToNearby(PacketTarget.TargetPoint target) {
        return new PacketTarget((packet, register) -> {
            MinecraftServer server = FabricServerLifecycleEvents.getServer();
            if (server != null) {
                ServerLevel level = server.getLevel(target.key);
                if (level != null) {
                    double radiusSq = target.radius * target.radius;
                    level.players()
                            .stream()
                            .filter(player -> player != target.excluded)
                            .filter(player -> player.distanceToSqr(target.pos) <= radiusSq)
                            .toList()
                            .forEach(
                                    player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
                            );

                }
            } else {
                System.out.println("No Server!");
            }
        });
    }


    @Override
    public PacketTarget sendToAll() {
        return new PacketTarget((packet, register) -> {
            MinecraftServer server = FabricServerLifecycleEvents.getServer();
            if (server != null) {
                PlayerLookup.all(server).forEach(
                        player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
                );
            } else {
                System.out.println("No Server!");
            }
        });
    }

    @Override
    public PacketTarget sendToTrackingEntity(Entity entity) {
        return new PacketTarget((packet, register) -> {
            PlayerLookup.tracking(entity).forEach(
                    player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
            );
        });
    }

    @Override
    public PacketTarget sendToTrackingEntityAndSelf(Entity entity) {
        return new PacketTarget((packet, register) -> {
            PlayerLookup.tracking(entity).forEach(
                    player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
            );
            // Sends to the entity itself, if it's a ServerPlayer
            if (entity instanceof ServerPlayer player)
                ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet));
        });
    }

    @Override
    public PacketTarget sendToTrackingChunk(LevelChunk chunk) {
        return new PacketTarget((packet, register) -> {
            ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(
                    player -> ServerPlayNetworking.send(player, ((FabricPacketRegister) register).channel, register.encode(packet))
            );
        });
    }
}
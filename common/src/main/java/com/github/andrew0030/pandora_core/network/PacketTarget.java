package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.BiConsumer;

public class PacketTarget {
    private static final PacketTarget SERVER_TARGET = Services.NETWORK.sendToServer();
    private final BiConsumer<Packet, PacketRegister> sender;

    public PacketTarget(BiConsumer<Packet, PacketRegister> sender) {
        this.sender = sender;
    }

    public void send(Packet packet, PacketRegister register) {
        this.sender.accept(packet, register);
    }

    // ######### Packet Targets #########

    /**
     * @return a {@link PacketTarget} representing the {@code server}.
     */
    public static PacketTarget sendToServer() {
        return PacketTarget.SERVER_TARGET;
    };

    /**
     * @param player the {@link ServerPlayer} to target.
     * @return a {@link PacketTarget} representing the given player.
     */
    public static PacketTarget sendToPlayer(ServerPlayer player) {
        return Services.NETWORK.sendToPlayer(player);
    }

    /**
     * @param dimension the {@link ResourceKey} representing the dimension to target.
     * @return a {@link PacketTarget} representing everyone in the given dimension.
     */
    public static PacketTarget sendToDimension(ResourceKey<Level> dimension) {
        return Services.NETWORK.sendToDimension(dimension);
    }

    // TODO maybe add this one
//    public static PacketTarget sendToNearby()

    /**
     * @return a {@link PacketTarget} representing all players.
     */
    public static PacketTarget sendToAll() {
        return Services.NETWORK.sendToAll();
    }

    /**
     * @param entity the {@link Entity} to target.
     * @return a {@link PacketTarget} representing everyone tracking the given entity.
     */
    public static PacketTarget sendToTrackingEntity(Entity entity) {
        return Services.NETWORK.sendToTrackingEntity(entity);
    }

    /**
     * @param entity the {@link Entity} to target.
     * @return a {@link PacketTarget} representing everyone tracking the given entity, and the entity itself.
     * @implNote the entity has to be a {@link ServerPlayer} to receive the packet.
     */
    public static PacketTarget sendToTrackingEntityAndSelf(Entity entity) {
        return Services.NETWORK.sendToTrackingEntityAndSelf(entity);
    }

    /**
     * @param chunk the {@link LevelChunk} to target.
     * @return a {@link PacketTarget} representing everyone tracking the given chunk.
     */
    public static PacketTarget sendToTrackingChunk(LevelChunk chunk) {
        return Services.NETWORK.sendToTrackingChunk(chunk);
    }
}
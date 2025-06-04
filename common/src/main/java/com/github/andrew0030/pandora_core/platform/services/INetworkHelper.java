package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.network.PacketRegister;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;

public interface INetworkHelper {

    PacketRegister getPacketRegistry(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker);

    PacketTarget sendToServer();

    PacketTarget sendToPlayer(ServerPlayer player);

    PacketTarget sendToDimension(ResourceKey<Level> dimension);

    PacketTarget sendToAll();

    PacketTarget sendToTrackingEntity(Entity entity);

    PacketTarget sendToTrackingEntityAndSelf(Entity entity);

    PacketTarget sendToTrackingChunk(LevelChunk chunk);
}
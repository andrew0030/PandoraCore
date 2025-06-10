package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class PacketTarget {
    private static final PacketTarget SERVER_TARGET = Services.NETWORK.sendToServer();
    private final BiConsumer<PaCoPacket, PacketRegister> sender;

    public PacketTarget(BiConsumer<PaCoPacket, PacketRegister> sender) {
        this.sender = sender;
    }

    public void send(PaCoPacket packet, PacketRegister register) {
        this.sender.accept(packet, register);
    }

    // ######### PaCoPacket Targets #########

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

    /**
     * @param target the {@link TargetPoint} to check around of.
     * @return a {@link PacketTarget} representing everyone within range of the target point.
     */
    public static PacketTarget sendToNearby(TargetPoint target) {
        return Services.NETWORK.sendToNearby(target);
    }

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

    public static class TargetPoint {
        public final @Nullable ServerPlayer excluded;
        public final ResourceKey<Level> key;
        public final Vec3 pos;
        public final double radius;

        /**
         * A target point with excluded entity.
         *
         * @param excluded the {@link ServerPlayer} to exclude
         * @param key      the {@link ResourceKey} of the {@link Level} to check in
         * @param pos      the position
         * @param radius   the maximum distance from the position in blocks
         */
        public TargetPoint(@Nullable ServerPlayer excluded, @NotNull ResourceKey<Level> key, @NotNull Vec3 pos, double radius) {
            this.excluded = excluded;
            this.key = key;
            this.pos = pos;
            this.radius = radius;
        }

        /**
         * A target point with excluded entity.
         *
         * @param excluded the {@link ServerPlayer} to exclude
         * @param key      the {@link ResourceKey} of the {@link Level} to check in
         * @param pos      the position (can be a {@link BlockPos})
         * @param radius   the maximum distance from the position in blocks
         * @implNote To target the center of a {@link BlockPos} use the {@link Vec3} constructor<br/>
         *           and call {@link Vec3#atCenterOf(Vec3i)} passing the {@link BlockPos}.
         */
        public TargetPoint(@Nullable ServerPlayer excluded, @NotNull ResourceKey<Level> key, @NotNull Vec3i pos, double radius) {
            this(excluded, key, new Vec3(pos.getX(), pos.getY(), pos.getZ()), radius);
        }

        /**
         * A target point without excluded entity.
         *
         * @param key      the {@link ResourceKey} of the {@link Level} to check in
         * @param pos      the position
         * @param radius   the maximum distance from the position in blocks
         */
        public TargetPoint(@NotNull ResourceKey<Level> key, @NotNull Vec3 pos, double radius) {
            this(null, key, pos, radius);
        }

        /**
         * A target point without excluded entity.
         *
         * @param key    the {@link ResourceKey} of the {@link Level} to check in
         * @param pos    the position (can be a {@link BlockPos})
         * @param radius the maximum distance from the position in blocks
         * @implNote To target the center of a {@link BlockPos} use the {@link Vec3} constructor<br/>
         *           and call {@link Vec3#atCenterOf(Vec3i)} passing the {@link BlockPos}.
         */
        public TargetPoint(@NotNull ResourceKey<Level> key, @NotNull Vec3i pos, double radius) {
            this(null, key, pos, radius);
        }
    }
}
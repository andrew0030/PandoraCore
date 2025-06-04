package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PacketRegister {
    public final ResourceLocation channel;

    protected PacketRegister(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        this.channel = name;
    }

    public static PacketRegister of(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        return Services.NETWORK.getPacketRegistry(name, networkVersion, clientChecker, serverChecker);
    }

    public abstract net.minecraft.network.protocol.Packet<?> toVanillaPacket(Packet wrapperPacket, NetworkDirection toClient);

    public abstract <T extends Packet> void registerMessage(int index, Class<T> clazz, BiConsumer<Packet, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<Packet, NetCtx> handler);

    public abstract void send(PacketTarget target, Packet packet);

    public abstract int getId(Packet packet);

    public abstract FriendlyByteBuf encode(Packet packet);
}
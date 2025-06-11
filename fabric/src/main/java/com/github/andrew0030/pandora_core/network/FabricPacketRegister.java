package com.github.andrew0030.pandora_core.network;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FabricPacketRegister extends PaCoPacketChannel {
    public final ResourceLocation channel;
    private final Int2ObjectOpenHashMap<PacketEntry<?>> entries = new Int2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Class<? extends PaCoPacket>> class2IdMap = new Object2IntOpenHashMap<>();

    public FabricPacketRegister(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        this.channel = name;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(
                    name,
                    ((client, handler, buf, responseSender) -> {
                        this.handlePacket(handler, buf, new PacketSender(this, (packet) -> responseSender.sendPacket(name, this.encode(packet))), null, NetworkDirection.TO_CLIENT);
                    })
            );
        }
        ServerPlayNetworking.registerGlobalReceiver(
                name,
                ((server, player, handler, buf, responseSender) -> {
                    this.handlePacket(handler, buf, new PacketSender(this, (packet) -> responseSender.sendPacket(name, this.encode(packet))), player, NetworkDirection.TO_SERVER);
                })
        );
    }

    private void handlePacket(PacketListener handler, FriendlyByteBuf buf, PacketSender responseSender, ServerPlayer player, NetworkDirection direction) {
        int id = buf.readByte();
        PacketEntry<?> entry = entries.get(id);
        PaCoPacket packet = entry.fabricator.apply(buf);
        packet.handle(new FabricNetCtx(handler, responseSender, player, direction));
    }

    @Override
    public Packet<?> toVanillaPacket(PaCoPacket wrapperPacket, NetworkDirection toClient) {
        FriendlyByteBuf buf = this.encode(wrapperPacket);
        return switch (toClient) {
            case TO_CLIENT -> ServerPlayNetworking.createS2CPacket(this.channel, buf);
            case TO_SERVER -> ClientPlayNetworking.createC2SPacket(this.channel, buf);
        };
    }

    @Override
    public <T extends PaCoPacket> void registerMessage(int index, Class<T> clazz, BiConsumer<PaCoPacket, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<PaCoPacket, NetCtx> handler) {
        this.entries.put(index, new PacketEntry<>(writer, fabricator));
        this.class2IdMap.put(clazz, index);
    }

    @Override
    public FriendlyByteBuf encode(PaCoPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        int id = this.class2IdMap.getInt(packet.getClass());
        buf.writeByte(id & 255);
        this.entries.get(id).writer.accept(packet, buf);
        return buf;
    }

    private record PacketEntry<T extends PaCoPacket>(BiConsumer<PaCoPacket, FriendlyByteBuf> writer,
                                                     Function<FriendlyByteBuf, T> fabricator) {
    }
}
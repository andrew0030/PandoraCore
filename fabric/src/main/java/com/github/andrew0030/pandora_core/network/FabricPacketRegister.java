package com.github.andrew0030.pandora_core.network;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class FabricPacketRegister extends PaCoPacketRegistry {
    public final ResourceLocation channel;
    private final Int2ObjectOpenHashMap<PaCoPacketType<?>> idToType = new Int2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Class<? extends PaCoPacket>> classToId = new Object2IntOpenHashMap<>();

    public FabricPacketRegister(ResourceLocation name) {
        this.channel = name;
    }

    @Override
    public void register() {
        int index = 0;
        for (PaCoPacketType<?> type : this.packets) {
            idToType.put(index, type);
            classToId.put(type.packetClass, index);
            index++;
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(this.channel, (client, handler, buf, responseSender) -> {
                handlePacket(handler, buf, null, PacketFlow.CLIENTBOUND,
                        packet -> responseSender.sendPacket(this.channel, encode(packet)));
            });
        }

        ServerPlayNetworking.registerGlobalReceiver(this.channel, (server, player, handler, buf, responseSender) -> {
            handlePacket(handler, buf, player, PacketFlow.SERVERBOUND,
                    packet -> responseSender.sendPacket(this.channel, encode(packet)));
        });
    }

    @SuppressWarnings("unchecked")
    private void handlePacket(PacketListener listener, FriendlyByteBuf buf, ServerPlayer player, PacketFlow expectedFlow, Consumer<PaCoPacket> replyAction) {
        int id = buf.readByte() & 255;
        PaCoPacketType<PaCoPacket> type = (PaCoPacketType<PaCoPacket>) idToType.get(id);

        if (type == null) {
            System.err.println("[PaCoNet] Received unknown packet ID: " + id);
            return;
        }

        // SECURITY CHECK
        if (type.flow != expectedFlow) { //TODO: Add bidirectional check
//            System.err.println("[PaCoNet] Security Warning: Dropped spoofed packet '" +
//                    type.packetClass.getSimpleName() + "'. Expected: " + type.flow +
//                    ", Received on: " + expectedFlow);
            return;
        }

        PaCoPacket packet = type.reader.apply(buf);

        PacketContext context = new PacketContext(
                listener,
                player,
                expectedFlow,
                runnable -> {
                    if (player != null && player.getServer() != null) {
                        player.getServer().execute(runnable);
                    } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                        Minecraft.getInstance().tell(runnable);
                    } else {
                        runnable.run();
                    }
                },
                replyAction
        );

        type.handler.handle(packet, context);
    }

    @SuppressWarnings("unchecked")
    public FriendlyByteBuf encode(PaCoPacket packet) {
        if (!classToId.containsKey(packet.getClass())) {
            throw new IllegalArgumentException("Attempted to send unregistered packet: " + packet.getClass().getSimpleName());
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        int id = classToId.getInt(packet.getClass());

        buf.writeByte(id & 255);

        PaCoPacketType<PaCoPacket> type = (PaCoPacketType<PaCoPacket>) idToType.get(id);
        type.writer.accept(packet, buf);

        return buf;
    }
}
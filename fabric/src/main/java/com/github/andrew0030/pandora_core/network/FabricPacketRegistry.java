package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class FabricPacketRegistry extends PaCoPacketRegistry {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "FabricPacketRegistry");
    public final ResourceLocation channel;
    private final Int2ObjectOpenHashMap<PaCoPacketType<?>> idToType = new Int2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Class<? extends PaCoPacket>> classToId = new Object2IntOpenHashMap<>();

    public FabricPacketRegistry(ResourceLocation name) {
        this.channel = name;
    }

    @Override
    public void register() {
        int index = 0;
        for (PaCoPacketType<?> type : this.packets) {
            this.idToType.put(index, type);
            this.classToId.put(type.packetClass(), index);
            index++;
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(this.channel, (client, handler, buf, responseSender) -> {
                this.handlePacket(handler, buf, null, PaCoPacketFlow.CLIENTBOUND, packet -> {
                    responseSender.sendPacket(this.channel, this.encode(packet));
                });
            });
        }

        ServerPlayNetworking.registerGlobalReceiver(this.channel, (server, player, handler, buf, responseSender) -> {
            this.handlePacket(handler, buf, player, PaCoPacketFlow.SERVERBOUND, packet -> {
                responseSender.sendPacket(this.channel, this.encode(packet));
            });
        });
    }

    @SuppressWarnings("unchecked")
    private void handlePacket(PacketListener listener, FriendlyByteBuf buf, ServerPlayer player, PaCoPacketFlow flow, Consumer<PaCoPacket> replyAction) {
        int id = buf.readByte() & 255;
        PaCoPacketType<PaCoPacket> type = (PaCoPacketType<PaCoPacket>) idToType.get(id);
        // This should never really happen but just in case...
        if (type == null) {
            LOGGER.error("Received unknown packet ID: {}", id);
            return;
        }
        // Prevents packet spoofing by checking if the receiving side matches the packets expected side
        if (type.flow() != PaCoPacketFlow.BIDIRECTIONAL && type.flow() != flow) {
            LOGGER.warn("Security Warning: Dropped spoofed packet '{}'. Expected flow: {}, but received on: {}", type.packetClass().getSimpleName(), type.flow(), flow);
            return;
        }
        PaCoPacket packet = type.reader().apply(buf);
        PacketContext context = new PacketContext(
                listener,
                player,
                flow,
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
        type.handler().handle(packet, context);
    }

    @SuppressWarnings("unchecked")
    public FriendlyByteBuf encode(PaCoPacket packet) {
        PaCoPacketType<PaCoPacket> type = (PaCoPacketType<PaCoPacket>) packet.getType();
        if (!classToId.containsKey(type.packetClass()))
            throw new IllegalArgumentException("Attempted to send unregistered packet: " + type.packetClass().getSimpleName());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        int id = classToId.getInt(type.packetClass());
        buf.writeByte(id & 255);
        type.writer().accept(packet, buf);
        return buf;
    }
}
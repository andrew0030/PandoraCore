package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.PandoraCore;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ForgePacketRegister extends PacketRegister {
    Int2ObjectOpenHashMap<PacketEntry<?>> entries = new Int2ObjectOpenHashMap<>();
    Object2IntOpenHashMap<Class<? extends Packet>> class2IdMap = new Object2IntOpenHashMap<>();
    public final SimpleChannel channel;

    public ForgePacketRegister(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        super(name, networkVersion, clientChecker, serverChecker);
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(PandoraCore.MOD_ID, "main"), //TODO figure out a clean way to pass info to this
                () -> networkVersion,
                clientChecker, serverChecker
        );
    }

    private void handlePacket(PacketListener handler, FriendlyByteBuf buf, PacketSender responseSender, ServerPlayer player, NetworkDirection direction, NetworkEvent.Context context) {
        int id = buf.readByte();
        PacketEntry<?> entry = entries.get(id);
        Packet packet = entry.fabricator.apply(buf);
        packet.handle(new ForgeNetCtx(handler, responseSender, player, direction, context));
    }

    @Override
    public net.minecraft.network.protocol.Packet<?> toVanillaPacket(Packet wrapperPacket, NetworkDirection toClient) {
        return switch (toClient) {
            case TO_CLIENT -> this.channel.toVanillaPacket(wrapperPacket, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
            case TO_SERVER -> this.channel.toVanillaPacket(wrapperPacket, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER);
        };
    }

    @Override
    public <T extends Packet> void registerMessage(int index, Class<T> clazz, BiConsumer<Packet, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<Packet, NetCtx> handler) {
        this.entries.put(index, new PacketEntry<>(clazz, writer, fabricator, handler));
        this.class2IdMap.put(clazz, index);

        this.channel.registerMessage(
            index,
            clazz,
            writer::accept,
            fabricator,
            (pkt, ctxSupplier) -> {
                NetworkEvent.Context context = ctxSupplier.get();
                NetworkDirection direction = switch (context.getDirection()) {
                    case PLAY_TO_CLIENT, LOGIN_TO_CLIENT -> NetworkDirection.TO_CLIENT;
                    case PLAY_TO_SERVER, LOGIN_TO_SERVER -> NetworkDirection.TO_SERVER;
                };

                ForgeNetCtx forgeContext = new ForgeNetCtx(
                    context.getNetworkManager().getPacketListener(),
                    new PacketSender(this, pktToSend -> {
                        if (context.getDirection().getReceptionSide().isServer()) {
                            this.channel.send(PacketDistributor.PLAYER.with(context::getSender), pktToSend);
                        } else {
                            this.channel.sendToServer(pktToSend);
                        }
                    }),
                    context.getSender(),
                    direction,
                    context
                );

                handler.accept(pkt, forgeContext);
            }
        );
    }

    @Override
    public void send(PacketTarget target, Packet packet) {
        target.send(packet, this);
    }

    @Override
    public int getId(Packet packet) {
        return this.class2IdMap.getInt(packet.getClass());
    }

    @Override
    public FriendlyByteBuf encode(Packet packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        this.channel.encodeMessage(packet, buf);
        return buf;
    }

    private static class PacketEntry<T extends Packet> {
        Class<T> clazz;
        BiConsumer<Packet, FriendlyByteBuf> writer;
        Function<FriendlyByteBuf, T> fabricator;
        BiConsumer<Packet, NetCtx> handler;

        public PacketEntry(Class<T> clazz, BiConsumer<Packet, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<Packet, NetCtx> handler) {
            this.clazz = clazz;
            this.writer = writer;
            this.fabricator = fabricator;
            this.handler = handler;
        }
    }
}
package com.github.andrew0030.pandora_core.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ForgePacketRegister extends PaCoPacketChannel {
    public final SimpleChannel channel;

    public ForgePacketRegister(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        this.channel = NetworkRegistry.newSimpleChannel(name, () -> networkVersion, clientChecker, serverChecker);
    }

    @Override
    public Packet<?> toVanillaPacket(PaCoPacket packet, NetworkDirection direction) {
        return switch (direction) {
            case TO_CLIENT -> this.channel.toVanillaPacket(packet, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
            case TO_SERVER -> this.channel.toVanillaPacket(packet, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER);
        };
    }

    @Override
    public <T extends PaCoPacket> void registerMessage(int index, Class<T> clazz, BiConsumer<PaCoPacket, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<PaCoPacket, NetCtx> handler) {

        this.channel.registerMessage(
            index,
            clazz,
            writer::accept,
            fabricator,
            (packet, ctxSupplier) -> {
                NetworkEvent.Context context = ctxSupplier.get();
                NetworkDirection direction = switch (context.getDirection()) {
                    case PLAY_TO_CLIENT, LOGIN_TO_CLIENT -> NetworkDirection.TO_CLIENT;
                    case PLAY_TO_SERVER, LOGIN_TO_SERVER -> NetworkDirection.TO_SERVER;
                };

                ForgeNetCtx forgeContext = new ForgeNetCtx(
                    context.getNetworkManager().getPacketListener(),
                    new PacketSender(this, packetToSend -> {
                        PacketDistributor.PacketTarget target = context.getDirection().getReceptionSide().isServer() ?
                                PacketDistributor.PLAYER.with(context::getSender) :
                                PacketDistributor.SERVER.noArg();
                        this.channel.send(target, packetToSend);
                    }),
                    context.getSender(),
                    direction,
                    context
                );

                handler.accept(packet, forgeContext);
            }
        );
    }

    @Override
    public FriendlyByteBuf encode(PaCoPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        this.channel.encodeMessage(packet, buf);
        return buf;
    }
}
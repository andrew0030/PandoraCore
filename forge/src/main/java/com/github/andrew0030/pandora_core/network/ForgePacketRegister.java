package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.PandoraCore;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ForgePacketRegister extends PacketRegister {
    public final SimpleChannel channel;

    public ForgePacketRegister(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        super(name, networkVersion, clientChecker, serverChecker);
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(PandoraCore.MOD_ID, "main"), //TODO figure out a clean way to pass info to this
                () -> networkVersion,
                clientChecker, serverChecker
        );
    }

//    @Override
//    public net.minecraft.network.protocol.Packet<?> toVanillaPacket(Packet wrapperPacket, NetworkDirection toClient) {
//        return switch (toClient) {
//            case TO_CLIENT -> this.channel.toVanillaPacket(wrapperPacket, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
//            case TO_SERVER -> this.channel.toVanillaPacket(wrapperPacket, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER);
//        };
//    }

    @Override
    public <T extends Packet> void registerMessage(int index, Class<T> clazz, BiConsumer<Packet, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<Packet, NetCtx> handler) {

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
        return 0;
    }

    @Override
    public FriendlyByteBuf encode(Packet packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        this.channel.encodeMessage(packet, buf);
        return buf;
    }
}
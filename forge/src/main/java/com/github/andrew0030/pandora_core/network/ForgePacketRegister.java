package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgePacketRegister extends PaCoPacketRegistry {
    public final SimpleChannel channel;

    public ForgePacketRegister(ResourceLocation name) {
        String version = "1.0.0";
        this.channel = NetworkRegistry.newSimpleChannel(name, () -> version, version::equals, version::equals);
    }

    @Override
    public void register() {
        int index = 0;
        for (PaCoPacketType<?> packet : this.packets)
            this.registerInternal(index++, packet);
    }

    private <T extends PaCoPacket> void registerInternal(int index, PaCoPacketType<T> type) {
        this.channel.registerMessage(
            index,
            type.packetClass,
            type.writer,
            type.reader,
            (packet, ctxSupplier) -> {
                NetworkEvent.Context forgeCtx = ctxSupplier.get();
                // Determines the direction the packet just traveled
                PacketFlow flow = forgeCtx.getDirection().getReceptionSide().isServer() ? PacketFlow.SERVERBOUND : PacketFlow.CLIENTBOUND;
                // Prevents packet spoofing by checking
                if (type.flow != flow) { // TODO: Add bidirectional logic
//                    System.err.println("[PaCoNet] Security Warning: Dropped spoofed packet '" +
//                            type.packetClass.getSimpleName() + "'. Expected flow: " +
//                            type.flow + ", but received on: " + flow);
                    forgeCtx.setPacketHandled(true);
                    return;
                }
                PacketContext context = new PacketContext(
                        forgeCtx.getNetworkManager().getPacketListener(),
                        forgeCtx.getSender(),
                        flow,
                        forgeCtx::enqueueWork,
                        packetToSend -> {
                            PacketDistributor.PacketTarget target = forgeCtx.getDirection().getReceptionSide().isServer() ?
                                    PacketDistributor.PLAYER.with(forgeCtx::getSender) :
                                    PacketDistributor.SERVER.noArg();
                            this.channel.send(target, packetToSend);
                        }
                );
                type.handler.handle(packet, context);
                forgeCtx.setPacketHandled(true);
            }
        );
    }
}
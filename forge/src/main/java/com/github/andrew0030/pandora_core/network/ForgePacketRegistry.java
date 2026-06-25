package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

public class ForgePacketRegistry extends PaCoPacketRegistry {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ForgePacketRegistry");
    public final SimpleChannel channel;

    public ForgePacketRegistry(ResourceLocation name) {
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
            type.packetClass(),
            type.writer(),
            type.reader(),
            (packet, ctxSupplier) -> {
                NetworkEvent.Context forgeCtx = ctxSupplier.get();
                // Determines the direction the packet just traveled
                PaCoPacketFlow flow = forgeCtx.getDirection().getReceptionSide().isServer() ? PaCoPacketFlow.SERVERBOUND : PaCoPacketFlow.CLIENTBOUND;
                // Prevents packet spoofing by checking if the receiving side matches the packets expected side
                if (type.flow() != PaCoPacketFlow.BIDIRECTIONAL && type.flow() != flow) {
                    LOGGER.warn("Security Warning: Dropped spoofed packet '{}'. Expected flow: {}, but received on: {}", type.packetClass().getSimpleName(), type.flow(), flow);
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
                type.handler().handle(packet, context);
                forgeCtx.setPacketHandled(true);
            }
        );
    }
}
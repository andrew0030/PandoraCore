package com.github.andrew0030.pandora_core.test.networking.packet.serverbound;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.PaCoMainConfig;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.network.PaCoPacket;
import com.github.andrew0030.pandora_core.network.PaCoPacketFlow;
import com.github.andrew0030.pandora_core.network.PaCoPacketType;
import com.github.andrew0030.pandora_core.test.networking.packet.clientbound.OpenTestGUIPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record KeyPressedPacket(int key) implements PaCoPacket {
    public static final PaCoPacketType<KeyPressedPacket> TYPE = new PaCoPacketType<>(
        KeyPressedPacket.class,
        PaCoPacketFlow.SERVERBOUND,
        new ResourceLocation(PandoraCore.MOD_ID, "key_pressed"),
        (packet, buf) -> buf.writeInt(packet.key),
        (buf) -> new KeyPressedPacket(buf.readInt()),
        (packet, context) -> {
            context.enqueue(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
//                    int grassMined = player.getStats().getValue(Stats.ITEM_USED.get(Blocks.GRASS_BLOCK.asItem()));
//                    player.sendSystemMessage(Component.literal("Grass Blocks Placed: " + grassMined));
//                    player.sendSystemMessage(Component.literal("Pressed Key: " + packet.key));
//                    context.reply(new OpenTestGUIPacket());

                    player.sendSystemMessage(Component.literal("Server Side Value: " + PaCoMainConfig.integerValue1));
                    player.sendSystemMessage(Component.literal("Adding +1 Server Side..."));
                    PaCoConfigManager manager = PaCoConfigManager.getManager(PaCoMainConfig.class);
                    manager.getConfig().set("integerValue1", PaCoMainConfig.integerValue1 + 1);
                    manager.correctIfNeeded(true);

                    context.reply(new OpenTestGUIPacket());
                }
            });
        }
    );

    @Override
    public PaCoPacketType<?> getType() {
        return TYPE;
    }
}
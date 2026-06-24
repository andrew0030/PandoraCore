package com.github.andrew0030.pandora_core.test.networking.packet.c2s;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.network.PaCoPacket;
import com.github.andrew0030.pandora_core.network.PaCoPacketType;
import com.github.andrew0030.pandora_core.network.PacketTarget;
import com.github.andrew0030.pandora_core.test.networking.PaCoNetworking;
import com.github.andrew0030.pandora_core.test.networking.packet.s2c.OpenTestGUIPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;

public record KeyPressedPacket(int key) implements PaCoPacket {
    public static final PaCoPacketType<KeyPressedPacket> TYPE = new PaCoPacketType<>(
        KeyPressedPacket.class,
        PacketFlow.SERVERBOUND,
        new ResourceLocation(PandoraCore.MOD_ID, "key_pressed"),
        (packet, buf) -> buf.writeInt(packet.key),
        (buf) -> new KeyPressedPacket(buf.readInt()),
        (packet, context) -> {
            context.enqueue(() -> {
                ServerPlayer player = context.sender;
                if (player != null) {
                    int grassMined = player.getStats().getValue(Stats.ITEM_USED.get(Blocks.GRASS_BLOCK.asItem()));
                    player.sendSystemMessage(Component.literal("Grass Blocks Placed: " + grassMined));
                    player.sendSystemMessage(Component.literal("Pressed Key: " + packet.key));

                    OpenTestGUIPacket packet1 = new OpenTestGUIPacket();
                    context.reply(packet1);
                }
            });
        }
    );

    @Override
    public PaCoPacketType<?> getType() {
        return TYPE;
    }
}
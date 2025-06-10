package com.github.andrew0030.pandora_core.test.networking.packet.c2s;

import com.github.andrew0030.pandora_core.network.NetCtx;
import com.github.andrew0030.pandora_core.network.PaCoPacket;
import com.github.andrew0030.pandora_core.test.networking.packet.s2c.OpenTestGUIPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;

public class KeyPressedPacket extends PaCoPacket {
    private final int key;

    public KeyPressedPacket(int key) {
        this.key = key;
    }

    public KeyPressedPacket(FriendlyByteBuf buf) {
        this.key = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.key);
    }

    @Override
    public void handle(NetCtx context) {
        context.enqueueWork(() -> {
            if (context.checkServer()) {
                ServerPlayer player = context.getSender();
                int grassMined = player.getStats().getValue(Stats.ITEM_USED.get(Blocks.GRASS_BLOCK.asItem()));
                player.sendSystemMessage(Component.literal("Grass Blocks Placed: " + grassMined));

                OpenTestGUIPacket packet = new OpenTestGUIPacket();
                context.respond(packet);
            }
        });
        context.setPacketHandled(true);
    }
}
package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class PaCoPacketRegistry {
    protected final List<PaCoPacketType<?>> packets = new ArrayList<>();

    protected PaCoPacketRegistry() {}

    public static PaCoPacketRegistry of(ResourceLocation name) {
        return Services.NETWORK.getPacketRegistry(name);
    }

    public void add(PaCoPacketType<?> type) {
        this.packets.add(type);
    }

    public abstract void register();

    /**
     * Sends a {@link PaCoPacket} to the specified {@link PacketTarget}.
     *
     * @param target the target destination for the packet
     * @param packet the packet to be sent
     */
    public void send(PacketTarget target, PaCoPacket packet) {
        target.send(packet, this);
    }
}
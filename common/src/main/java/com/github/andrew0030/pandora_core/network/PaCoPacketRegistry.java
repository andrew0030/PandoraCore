package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract networking channel for sending and receiving
 * {@link PaCoPacket PaCoPackets} across client and server.
 * <p>
 * <strong>Note</strong>: this system can also be used to send packets between different
 * loaders, however this behaviour requires some extra setup like a handshake packet.
 * </p>
 * Usage example:
 * <pre>{@code
 * public static final PaCoPacketRegistry CHANNEL = PaCoPacketRegistry.of(new ResourceLocation(ExampleMod.MOD_ID, "main"));
 *
 * static {
 *     // Server to Client
 *     CHANNEL.add(OpenGUIPacket.TYPE);
 *     // Client to Server
 *     CHANNEL.add(KeyPressedPacket.TYPE);
 * }
 * }</pre>
 *
 * <p>And then during mod construction:</p>
 * <pre>{@code
 * ExampleModNetworking.CHANNEL.register();
 * }</pre>
 *
 * And to send a packet:
 * <pre>{@code
 * ExamplePacket packet = new ExamplePacket();
 * TestNetworking.CHANNEL.send(PacketTarget.sendToServer(), packet);
 * }</pre>
 */
public abstract class PaCoPacketRegistry {
    protected final List<PaCoPacketType<? extends PaCoPacket>> packets = new ArrayList<>();

    protected PaCoPacketRegistry() {}

    /**
     * Creates a new {@link PaCoPacketRegistry} instance for the given {@link ResourceLocation}.
     *
     * @param name The {@link ResourceLocation} that identifies this network channel
     * @return A new configured {@link PaCoPacketRegistry}
     */
    public static PaCoPacketRegistry of(ResourceLocation name) {
        return Services.NETWORK.getPacketRegistry(name);
    }

    /**
     * Adds a {@link PaCoPacketType} to be registered.
     *
     * @param type The {@link PaCoPacketType} instance that will be registered
     * @see PaCoPacketType
     */
    public void add(PaCoPacketType<? extends PaCoPacket> type) {
        this.packets.add(type);
    }

    /**
     * This needs to be called, so entries are registered by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.<br/>
     */
    public abstract void register();

    /**
     * Sends a {@link PaCoPacket} to the specified {@link PacketTarget}.
     *
     * @param target The target destination for the packet
     * @param packet The packet to be sent
     */
    public void send(PacketTarget target, PaCoPacket packet) {
        target.send(packet, this);
    }
}
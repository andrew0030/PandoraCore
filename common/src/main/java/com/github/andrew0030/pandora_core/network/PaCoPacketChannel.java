package com.github.andrew0030.pandora_core.network;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents an abstract networking channel for sending and receiving
 * {@link PaCoPacket PaCoPackets} across client and server.
 * <p>
 * <strong>Note</strong>: this system can also be used to send packets between different
 * loaders, however this behaviour requires some extra setup like a handshake packet.
 * </p>
 * Usage example:
 * <pre>{@code
 * public static final String NETWORK_VERSION = "1.0.0";
 * public static final PaCoPacketChannel CHANNEL = PaCoPacketChannel.of(
 *         new ResourceLocation(ExampleMod.MOD_ID, "main"),
 *         NETWORK_VERSION,
 *         NETWORK_VERSION::equals,
 *         NETWORK_VERSION::equals
 * );
 *
 * static {
 *     // Server to Client
 *     PaCoPacketChannel.add(0, CHANNEL, OpenGUIPacket.class, OpenGUIPacket::new);
 *     // Client to Server
 *     PaCoPacketChannel.add(CHANNEL, KeyPressedPacket.class, KeyPressedPacket::new);
 * }
 * }</pre>
 *
 * <p>And then during mod construction:</p>
 * <pre>{@code
 * ExampleModNetworking.CHANNEL.register();
 * }</pre>
 */
public abstract class PaCoPacketChannel {
    private final List<Entry<? extends PaCoPacket>> entries = new ArrayList<>();

    /**
     * Creates a new {@link PaCoPacketChannel} instance for the given resource location and network versioning.
     *
     * @param name            the {@link ResourceLocation} that identifies this channel
     * @param networkVersion  the current version {@link String} of the network channel
     * @param clientChecker   a {@link Predicate} to check if the client's version is acceptable
     * @param serverChecker   a {@link Predicate} to check if the server's version is acceptable
     * @return a new configured {@link PaCoPacketChannel}
     */
    public static PaCoPacketChannel of(ResourceLocation name, String networkVersion, Predicate<String> clientChecker, Predicate<String> serverChecker) {
        return Services.NETWORK.getPacketRegistry(name, networkVersion, clientChecker, serverChecker);
    }

    /**
     * Adds a {@link PaCoPacket} to a specified {@link PaCoPacketChannel} to be registered.
     * <p>
     * This method can be used to manually specify the {@code index} a packet should have.<br/>
     * If manual specification isn't required use {@link PaCoPacketChannel#add(PaCoPacketChannel, Class, Function)}.
     *
     * @param index      the numeric ID used to identify the packet
     * @param channel    the {@link PaCoPacketChannel} the packet belongs to
     * @param clazz      the class type of the packet
     * @param fabricator a factory method to construct a packet instance from a {@link FriendlyByteBuf}
     * @apiNote <ul>
     *     <li>Any given {@code index} may only be used once! Otherwise an {@link IllegalStateException} will be thrown.</li>
     *     <li>The {@code index} should start at {@code 0}.</li>
     *     <li>For the {@code fabricator} use the packet's constructor that has a {@link FriendlyByteBuf}.</li>
     * </ul>
     */
    public static <T extends PaCoPacket> void add(int index, PaCoPacketChannel channel, Class<T> clazz, Function<FriendlyByteBuf, T> fabricator) {
        channel.entries.add(new Entry<>(index, clazz, fabricator));
    }

    /**
     * Adds a {@link PaCoPacket} to a specified {@link PaCoPacketChannel} to be registered.
     *
     * @param channel    the {@link PaCoPacketChannel} the packet belongs to
     * @param clazz      the class type of the packet
     * @param fabricator a factory method to construct a packet instance from a {@link FriendlyByteBuf}
     *
     * @apiNote  For the {@code fabricator} use the packet's constructor that has a {@link FriendlyByteBuf}.
     */
    public static <T extends PaCoPacket> void add(PaCoPacketChannel channel, Class<T> clazz, Function<FriendlyByteBuf, T> fabricator) {
        channel.entries.add(new Entry<>(null, clazz, fabricator));
    }

    /**
     * This needs to be called, so entries are registered by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ModInitializer#onInitialize}.<br/>
     *
     * @throws IllegalStateException if multiple packets with the same {@code index} were specified.
     */
    @SuppressWarnings("unchecked")
    public <T extends PaCoPacket> void register() {
        List<Entry<T>> defined = new ArrayList<>();
        List<Entry<T>> undefined = new ArrayList<>();
        for (Entry<?> entry : entries) {
            if (entry.index() != null) defined.add((Entry<T>) entry);
            else undefined.add((Entry<T>) entry);
        }
        Set<Integer> usedIndexes = new HashSet<>();

        // Registers entries with defined index
        for (Entry<T> entry : defined) {
            int index = entry.index();
            if (!usedIndexes.add(index))
                throw new IllegalStateException("Packet '" + entry.clazz().getSimpleName() + "' uses duplicate index: " + index);
            this.registerMessage(index, entry.clazz(), PaCoPacket::write, entry.fabricator(), PaCoPacket::handle);
        }

        // Registers entries with undefined index
        int nextIndex = 0;
        for (Entry<T> entry : undefined) {
            while (usedIndexes.contains(nextIndex))
                nextIndex++;
            this.registerMessage(nextIndex, entry.clazz(), PaCoPacket::write, entry.fabricator(), PaCoPacket::handle);
            usedIndexes.add(nextIndex++);
        }
    }

    /**
     * Converts a {@link PaCoPacket} to a native Minecraft {@link Packet}.
     *
     * @param packet    the {@link PaCoPacket} to convert
     * @param direction the {@link NetworkDirection} indicating where the packet is being sent
     * @return a Minecraft-native {@link Packet}
     */
    public abstract Packet<?> toVanillaPacket(PaCoPacket packet, NetworkDirection direction);

    /**
     * Registers a message (packet) within the networking channel.
     * <p>
     * This method binds the packet type to a unique index, and specifies how to
     * serialize, deserialize, and handle the packet when it's received.
     *
     * @param index      the numeric ID used to identify the packet
     * @param clazz      the class type of the {@link PaCoPacket} being registered
     * @param writer     a function that writes the packet data to a {@link FriendlyByteBuf}
     * @param fabricator a function that constructs the packet from a {@link FriendlyByteBuf}
     * @param handler    a function that handles the packet once received, given a {@link NetCtx}
     *
     * @apiNote Alternatively use the {@code static} {@link PaCoPacketChannel#add(int, PaCoPacketChannel, Class, Function)}.
     */
    public abstract <T extends PaCoPacket> void registerMessage(int index, Class<T> clazz, BiConsumer<PaCoPacket, FriendlyByteBuf> writer, Function<FriendlyByteBuf, T> fabricator, BiConsumer<PaCoPacket, NetCtx> handler);

    /**
     * Sends a {@link PaCoPacket} to the specified {@link PacketTarget}.
     *
     * @param target the target destination for the packet
     * @param packet the packet to be sent
     */
    public void send(PacketTarget target, PaCoPacket packet) {
        target.send(packet, this);
    }

    /**
     * Encodes the given {@link PaCoPacket} into a {@link FriendlyByteBuf}.
     *
     * @param packet the packet to encode
     * @return a {@link FriendlyByteBuf} containing the serialized packet data
     */
    public abstract FriendlyByteBuf encode(PaCoPacket packet);

    private record Entry<T extends PaCoPacket>(Integer index, Class<T> clazz, Function<FriendlyByteBuf, T> fabricator) {};
}
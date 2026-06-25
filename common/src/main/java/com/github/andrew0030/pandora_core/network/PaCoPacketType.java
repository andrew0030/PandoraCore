package com.github.andrew0030.pandora_core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents the metadata, serialization logic, and handling behavior for a specific {@link PaCoPacket} instance.
 * <p>
 * Every custom packet must implement the {@link PaCoPacket} interface and define a {@code public static final}
 * instance of this type. This instance is then registered to a {@link PaCoPacketRegistry} so the networking system
 * knows how to encode, decode, validate, and route the packet.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <p>To create a custom packet, define a {@code record} (or class) that implements {@link PaCoPacket}.
 * Inside the packet, create a static {@code TYPE} field that configures the packet's flow,
 * identifier, buffer serialization (writer/reader), and execution logic (handler).</p>
 *
 * <pre>{@code
 * public record KeyPressedPacket(int key) implements PaCoPacket {
 *     public static final PaCoPacketType<KeyPressedPacket> TYPE = new PaCoPacketType<>(
 *         KeyPressedPacket.class, // The packet class
 *         PaCoPacketFlow.SERVERBOUND, // The logical side this packet should be handled on
 *         new ResourceLocation(ExampleMod.MOD_ID, "key_pressed"), // The packet identifier
 *
 *         // Writer: Serializes the packet data into the buffer
 *         (packet, buf) -> buf.writeInt(packet.key),
 *
 *         // Reader: Deserializes the packet data from the buffer into a new instance
 *         // NOTE: It is important to read in the same order as the data was written!
 *         (buf) -> new KeyPressedPacket(buf.readInt()),
 *
 *         // Handler: The logic to execute when the packet is received
 *         (packet, context) -> {
 *             context.enqueue(() -> { // Defers execution to the main game thread.
 *                 // ... logic that should run on the server ...
 *                 ServerPlayer player = context.getSender();
 *                 if (player != null) {
 *                     // ... logic that requires the sender ...
 *
 *                     // (Optional) Sends a response back to the sender
 *                     context.reply(new OpenTestGUIPacket());
 *                 }
 *             });
 *         }
 *     );
 *
 *     @Override
 *     public PaCoPacketType<?> getType() {
 *         return TYPE;
 *     }
 * }
 * }</pre>
 *
 * @param packetClass The {@link Class} of the packet
 * @param flow        The {@link PaCoPacketFlow} direction this packet is allowed to travel
 * @param location    The unique {@link ResourceLocation} identifier for this packet
 * @param writer      A {@link BiConsumer} that defines how to serialize the packet's fields into a {@link FriendlyByteBuf}
 * @param reader      A {@link Function} that defines how to deserialize a new packet instance from a {@link FriendlyByteBuf}
 * @param handler     A {@link PayloadHandler} that defines the logic to execute when the packet is received
 */
public record PaCoPacketType<T extends PaCoPacket>(
        Class<T> packetClass, PaCoPacketFlow flow,
        ResourceLocation location, BiConsumer<T, FriendlyByteBuf> writer,
        Function<FriendlyByteBuf, T> reader, PayloadHandler<T> handler) {
}
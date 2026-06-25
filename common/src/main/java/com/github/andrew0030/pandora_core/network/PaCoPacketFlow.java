package com.github.andrew0030.pandora_core.network;

/** The direction of packets. */
public enum PaCoPacketFlow {
    /** Indicates that the packet should be handled by the {@code server}. */
    SERVERBOUND,
    /** Indicates that the packet should be handled by the {@code client}. */
    CLIENTBOUND,
    /** Indicates that the packet should be handled by the {@code server} and the {@code client}. */
    BIDIRECTIONAL;
}
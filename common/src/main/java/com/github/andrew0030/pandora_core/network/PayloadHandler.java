package com.github.andrew0030.pandora_core.network;

@FunctionalInterface
public interface PayloadHandler<T> {
    void handle(T t, PacketContext context);
}
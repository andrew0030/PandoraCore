package com.github.andrew0030.pandora_core.mixin_interfaces.container;

public interface IPaCoCheckInventory {
    default boolean pandoraCore$isInventory() {
        return false;
    }
}
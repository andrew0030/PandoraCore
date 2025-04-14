package com.github.andrew0030.pandora_core.platform.services;

import net.minecraft.core.Registry;

import java.util.Map;
import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> void register(Registry<T> key, String modId, Map<String, Supplier<T>> entries);
}
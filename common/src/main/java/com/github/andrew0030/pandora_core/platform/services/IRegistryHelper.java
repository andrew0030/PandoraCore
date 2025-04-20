package com.github.andrew0030.pandora_core.platform.services;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface IRegistryHelper {
    <T> void register(Registry<T> key, String modId, Map<String, Supplier<T>> entries);

    void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings);
}
package com.github.andrew0030.pandora_core.platform.services;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.KeyMapping;

import java.util.List;

public interface IKeyMappingHelper {
    void registerKeyMappings(List<Pair<KeyMapping, Runnable>> keyMappings);
}

package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.utils.LogicalSide;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.List;
import java.util.function.Function;

public interface IResourceLoaderHelper {
    void registerResourceLoader(Function<LogicalSide, List<ResourceManagerReloadListener>> function);
}

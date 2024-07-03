package com.github.andrew0030.pandora_core.platform.services;

import com.github.andrew0030.pandora_core.utils.LogicalSide;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.List;
import java.util.function.Function;

/**
 * Utilities for registering resource managers
 */
public interface IResourceLoaderHelper {
    void registerVanillaResourceLoader(Function<LogicalSide, List<ResourceManagerReloadListener>> function);
    void registerResourceLoader(Function<LogicalSide, List<PacoResourceManager>> function);
}

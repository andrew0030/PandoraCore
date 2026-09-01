package com.github.andrew0030.pandora_core.config.manager;

import java.util.Collection;

public interface IConfigManager {
    /** @return A flat collection of all {@link ConfigDataHolder} instances */
    Collection<ConfigDataHolder<?>> getDataHolders();

//    void save();
    String getModId();
    String getConfigName();
    default void close() {}
}
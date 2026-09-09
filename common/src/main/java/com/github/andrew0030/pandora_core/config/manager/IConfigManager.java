package com.github.andrew0030.pandora_core.config.manager;

import java.util.Collection;
import java.util.Map;

public interface IConfigManager {
    /** @return A flat collection of all {@link ConfigDataHolder} instances */
    Collection<ConfigDataHolder<?>> getDataHolders();
    // Config Stuff
    String getModId();
    String getConfigName();
    default void close() {}
    // Staging
    <T> void addPendingChange(ConfigDataHolder<T> holder, T pendingValue);
    Map<ConfigDataHolder<?>, Object> getPendingChanges();
    boolean hasPendingChanges();
    void clearPendingChanges();
    void savePendingChanges();
}
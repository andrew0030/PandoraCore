package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.forge_spec.FMLConfigHandler;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO maybe flag all config values as "requiresGameRestart"... That or just leave it as is, as most of not all values arent relevant past early boot
public class FMLConfigManager implements IConfigManager {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "FMLConfigManager");
    // Config Managing
    private final Map<ConfigDataHolder<?>, Object> pendingChanges = new HashMap<>();
    private final FMLConfigHandler handler;
    // Util
    private final String formatedName;

    public FMLConfigManager() {
        this.handler = new FMLConfigHandler();
        this.formatedName = "FML Config";
    }

    @Override
    public Collection<ConfigDataHolder<?>> getDataHolders() {
        return this.handler.getConfigDataHolders();
    }

    @Override
    public String getModId() {
        return "forge";
    }

    @Override
    public String getConfigName() {
        return this.formatedName;
    }

    @Override
    public <T> void addPendingChange(ConfigDataHolder<T> holder, T pendingValue) {
        if (!holder.hasValue()) return;
        @SuppressWarnings("unchecked")
        IConfigValueHolder<T> valueHolder = (IConfigValueHolder<T>) holder;
        T currentValue = valueHolder.getValue();
        if (Objects.equals(currentValue, pendingValue)) {
            this.pendingChanges.remove(holder);
        } else {
            this.pendingChanges.put(holder, pendingValue);
        }
    }

    @Override
    public Map<ConfigDataHolder<?>, Object> getPendingChanges() {
        return this.pendingChanges;
    }

    @Override
    public boolean hasPendingChanges() {
        return !this.pendingChanges.isEmpty();
    }

    @Override
    public void clearPendingChanges() {
        this.pendingChanges.clear();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void savePendingChanges() {
        if (!this.hasPendingChanges()) return;
        for (Map.Entry<ConfigDataHolder<?>, Object> pendingChange : this.pendingChanges.entrySet()) {
            ConfigDataHolder<?> holder = pendingChange.getKey();
            if (!holder.hasValue()) continue;
            Object pendingValue = pendingChange.getValue();
            ((FMLConfigDataHolderEntry) holder).setValue(pendingValue);
        }
        this.clearPendingChanges();
    }
}
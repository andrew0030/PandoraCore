package com.github.andrew0030.pandora_core.config.manager;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.forge_spec.ForgeConfigHandler;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

import java.util.*;

public class ForgeConfigManager implements IConfigManager {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ForgeConfigManager");
    // Config Managing
    private final Map<ConfigDataHolder<?>, Object> pendingChanges = new HashMap<>();
    private final ModConfig modConfig;
    private final ForgeConfigHandler handler;
    // Util
    private final String formatedName;

    public ForgeConfigManager(ModConfig modConfig) {
        this.modConfig = modConfig;
        this.handler = new ForgeConfigHandler(modConfig);
        String name = this.modConfig.getFileName().replace(this.getModId(), "").replace(".toml", "");
        if (!name.toLowerCase(Locale.ROOT).contains("config"))
            name = name + " config";
        this.formatedName = PaCoGuiUtils.toTitleCaseFormat(name);
    }

    @Override
    public Collection<ConfigDataHolder<?>> getDataHolders() {
        return this.handler.getConfigDataHolders();
    }

    @Override
    public String getModId() {
        return this.modConfig.getModId();
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
        // Applies all pending changes to Forge's ConfigValues
        for (Map.Entry<ConfigDataHolder<?>, Object> pendingChange : this.pendingChanges.entrySet()) {
            ConfigDataHolder<?> holder = pendingChange.getKey();
            if (!holder.hasValue()) continue;
            Object pendingValue = pendingChange.getValue();
            ((ForgeConfigDataHolderEntry) holder).setValue(pendingValue);
        }
        this.clearPendingChanges();
        // Tells Forge to clear its internal ConfigValue caches
        this.modConfig.getSpec().afterReload();
        // Fires the ModConfigEvent.Reloading event, ideally I would just use the method inside ModConfig, but that's package private, so this is the next best thing!
        ModList.get().getModContainerById(this.modConfig.getModId()).ifPresent(container -> container.dispatchConfigEvent(new ModConfigEvent.Reloading(this.modConfig)));
        // Saves the in-memory config to the disk
        this.modConfig.save();
    }
}
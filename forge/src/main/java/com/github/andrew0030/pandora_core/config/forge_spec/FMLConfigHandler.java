package com.github.andrew0030.pandora_core.config.forge_spec;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BooleanEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.IntegerEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.StringEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.UnsupportedEntry;
import com.github.andrew0030.pandora_core.config.access.FMLConfigAccess;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.FMLConfigDataHolderEntry;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.StringUtil;
import net.minecraftforge.fml.loading.FMLConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FMLConfigHandler {
    private static final Map<Class<?>, BiConsumer<FMLConfig.ConfigValue, String>> VALUE_HANDLERS = new HashMap<>();
    private final Map<String, ConfigDataHolder<?>> dataHolders = new LinkedHashMap<>();

    public FMLConfigHandler() {
        // Initializes the: VALUE_HANDLERS
        this.initConfigCaches();
    }

    /**
     * This method initializes the value handler caches.
     * <br/>
     * <strong>Note</strong>: This method ensures type safety.
     */
    private void initConfigCaches() {
        VALUE_HANDLERS.put(Boolean.class, this::handleBoolean); // Boolean
        VALUE_HANDLERS.put(Integer.class, this::handleInteger); // Integer
        VALUE_HANDLERS.put(String.class,  this::handleString);  // String
        VALUE_HANDLERS.put(List.class,    this::handleList);    // List

        this.processFMLConfig();
    }

    /** Handles loading all the values inside the fml config */
    private void processFMLConfig() {
        for (FMLConfig.ConfigValue value : FMLConfig.ConfigValue.values()) {
            Class<?> valueType = FMLConfigAccess.getValueType(value);
            BiConsumer<FMLConfig.ConfigValue, String> consumer = VALUE_HANDLERS.get(valueType);
            // Checks for explicit value types or falls back to standard classes
            if (consumer == null) {
                for (Map.Entry<Class<?>, BiConsumer<FMLConfig.ConfigValue, String>> entry : VALUE_HANDLERS.entrySet()) {
                    if (entry.getKey().isAssignableFrom(valueType)) {
                        consumer = entry.getValue();
                        break;
                    }
                }
            }
            // If any handler was found we run it
            if (consumer != null)
                consumer.accept(value, FMLConfigAccess.getEntry(value));
        }
    }

    /** @return an ordered {@link ImmutableList}, containing a {@link ConfigDataHolder} for each entry in fml.toml */
    public ImmutableList<ConfigDataHolder<?>> getConfigDataHolders() {
        return ImmutableList.copyOf(this.dataHolders.values());
    }

    // ######################################################################
    // #                           Value Handling                           #
    // ######################################################################

    private void handleBoolean(FMLConfig.ConfigValue value, String path) {
        @SuppressWarnings("unchecked")
        ConfigDataHolder<Boolean> holder = (ConfigDataHolder<Boolean>) this.dataHolders.getOrDefault(path, new FMLConfigDataHolderEntry<>(value));
        holder.setPath(path);
        holder.setConfigEntryFactory(BooleanEntry::new);
        this.extractMetadata(holder, value);
        this.dataHolders.put(path, holder);
    }

    private void handleInteger(FMLConfig.ConfigValue value, String path) {
        @SuppressWarnings("unchecked")
        ConfigDataHolder<Integer> holder = (ConfigDataHolder<Integer>) this.dataHolders.getOrDefault(path, new FMLConfigDataHolderEntry<>(value));
        holder.setPath(path);
        holder.setConfigEntryFactory(IntegerEntry::new);
        this.extractMetadata(holder, value);
        this.dataHolders.put(path, holder);
    }

    private void handleString(FMLConfig.ConfigValue value, String path) {
        @SuppressWarnings("unchecked")
        ConfigDataHolder<String> holder = (ConfigDataHolder<String>) this.dataHolders.getOrDefault(path, new FMLConfigDataHolderEntry<>(value));
        holder.setPath(path);
        holder.setConfigEntryFactory(StringEntry::new);
        this.extractMetadata(holder, value);
        this.dataHolders.put(path, holder);
    }

    private void handleList(FMLConfig.ConfigValue value, String path) {
        @SuppressWarnings("unchecked")
        ConfigDataHolder<List<?>> holder = (ConfigDataHolder<List<?>>) this.dataHolders.getOrDefault(path, new FMLConfigDataHolderEntry<>(value));
        holder.setPath(path);
        holder.setConfigEntryFactory(UnsupportedEntry::new); // TODO make this use the proper factory
        this.extractMetadata(holder, value);
        this.dataHolders.put(path, holder);
    }

    private void extractMetadata(ConfigDataHolder<?> holder, FMLConfig.ConfigValue value) {
        // Extracts the comment
        String comment = FMLConfigAccess.getComment(value);
        if (!StringUtil.isNullOrEmpty(comment)) holder.setComment(comment);
    }
}
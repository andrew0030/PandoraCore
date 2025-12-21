package com.github.andrew0030.pandora_core.config.manager;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PaCoConfigManager {

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoConfigManager");
    // A Map to store PaCoConfigManager instances
    private static final Map<Class<?>, PaCoConfigManager> CONFIG_MANAGERS = new HashMap<>();
    // Config Managing
    private final Class<?> configClass;                // The config class with the annotated fields
    private final AnnotationHandler annotationHandler; // Helper class that deals with annotations
    private final CommentedFileConfig config;          // The config file

    private PaCoConfigManager(Class<?> configClass) {
        this.configClass = configClass;
        this.annotationHandler = new AnnotationHandler(this);
        this.config = this.createEmptyConfig();
        this.loadAndCorrect(); // Loads the config and corrects it if needed
    }

    /**
     * Creates a {@link CommentedFileConfig} instance for the configuration file
     * based on the name specified in the {@link PaCoConfig} annotation.<br/>
     * Note: At this point the config is still "empty" and {@link CommentedFileConfig#load()}
     * still needs to be called to load the config from the file.
     *
     * @return a {@link CommentedFileConfig} instance
     */
    private CommentedFileConfig createEmptyConfig() {
        String configName = this.annotationHandler.getConfigName();
        String subFolder = this.annotationHandler.getConfigSubFolder();
        Path configDirectory = Services.PLATFORM.getConfigDirectory();
        Path targetDirectory = StringUtil.isNullOrEmpty(subFolder) ? configDirectory : configDirectory.resolve(subFolder);
        // Creates missing folders (needed to avoid FileNotFoundExceptions when sub-folders are used).
        try {
            Files.createDirectories(targetDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config directory: " + targetDirectory, e);
        }
        Path configFilePath = targetDirectory.resolve(configName + ".toml");
        // Creates the CommentedFileConfig instance
        CommentedFileConfigBuilder builder = CommentedFileConfig.builder(configFilePath);
        builder.preserveInsertionOrder();
        // Conditionally adds auto reloading to the config. We need this because forge has an outdated version of
        // Night Config, and thus we don't have access to the onAutoReload method. However, this is only the case
        // inside the IDE, as for production we shade the latest Night Config version, so the method becomes available.
        if (Services.PLATFORM.isDevelopmentEnvironment() && Services.PLATFORM.getPlatformName().equals("Forge")) {
            LOGGER.warn("[Forge IDE] detected, 'autoreload' for [{}] config has been disabled.", this.annotationHandler.getConfigName());
        } else {
            builder.autoreload();
        }
        // Adds an auto reload listener when possible. The only time this should fail is in a Forge IDE, as forge ships
        // an older version that doesn't have the method. But since we shade in production this should work just fine.
        try {
            Method onAutoReloadMethod = CommentedFileConfigBuilder.class.getMethod("onAutoReload", Runnable.class);
            onAutoReloadMethod.invoke(builder, this.autoReloadListener());
        } catch (Exception ignored) {}

        return builder.build();
    }

    private void correctIfNeeded() {
        ConfigSpec configSpec = this.annotationHandler.getConfigSpec();
        boolean isConfigCorrect = configSpec.isCorrect(this.config);
        // If the config isn't correct we handle it.
        if (!isConfigCorrect) {
            // List to store correction entries for formatted output
            List<CorrectionEntry> correctionEntries = new ArrayList<>();
            // Listener to store corrections made to the config in the correctionEntries list
            ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
                String pathString = String.join(".", path);
                correctionEntries.add(new CorrectionEntry(pathString, incorrectValue, correctedValue));
            };
            LOGGER.warn("Detected inconsistencies in [{}] config. Initiating corrections...", this.annotationHandler.getConfigName());
            int correctionCount = configSpec.correct(this.config, listener);
            // Calculates padding
            int maxKeyLength = correctionEntries.stream().mapToInt(e -> e.key.length()).max().orElse(0);
            int maxDetectedLength = correctionEntries.stream().mapToInt(e -> e.detected.length()).max().orElse(0);
            int maxCorrectedLength = correctionEntries.stream().mapToInt(e -> e.corrected.length()).max().orElse(0);
            // Logs each entry with aligned padding
            for (CorrectionEntry entry : correctionEntries)
                LOGGER.warn(" - Correction | Key: {} | Detected: {} | Corrected: {}",
                        this.padRight(entry.key, maxKeyLength),
                        this.padRight(entry.detected, maxDetectedLength),
                        this.padRight(entry.corrected, maxCorrectedLength)
                );
            LOGGER.warn("Correction Summary for [{}]: {} values adjusted.", this.annotationHandler.getConfigName(), correctionCount);
            // Orders config, this is needed because the config from .load() is unordered
            this.orderConfigEntries();
            // Adds the comments to the config
            this.setConfigComments();
            // Updates the values of the fields in memory
            this.updateConfigFields();
            // Saves the corrected and ordered config to the file, as the corrections need to be applied there as well
            this.config.save();
        } else {
            // Orders config, this is needed because the config from .load() is unordered
            // We don't need to call .save(), because in this case the file itself was correct,
            // and we simply order the "in memory" config for QoL
            this.orderConfigEntries();
            // Adds the comments to the config
            this.setConfigComments();
            // Updates the values of the fields in memory
            this.updateConfigFields();
        }
    }

    /** Utility method to pad Strings to a given length. */
    private String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    /**
     * Calls {@link FileConfig#load()} followed by validating the config using
     * the generated {@link ConfigSpec} and fixing the order of the entries.
     * This also calls {@link FileConfig#save()} if the config had to be corrected.
     */
    public void loadAndCorrect() {
        this.config.load();
        this.correctIfNeeded();
    }

    /** @return the <code>.class</code> of the config that was used to register this manager. */
    public Class<?> getConfigClass() {
        return this.configClass;
    }

    public CommentedFileConfig getConfig() {
        return this.config;
    }

    public static void closeConfigs() {
        for (PaCoConfigManager manager : PaCoConfigManager.getPaCoConfigManagers()) {
            manager.getConfig().close();
        }
    }

    /**
     * Re-orders the config entries, to match the annotation order of the given {@link PaCoConfig}.
     * <br/>
     * <strong>Note</strong>: this method doesn't perform any validation, so it should only be
     * used if the config has been validated and the entries in {@link PaCoConfigManager#config}
     * match the fields specified in {@link PaCoConfig}.
     */
    private void orderConfigEntries() {
        this.reorderTable(this.config, this.annotationHandler.getAnnotatedFields());
    }

    private void reorderTable(Config config, List<String> orderedKeys) {
        Map<String, Object> tempMap = new LinkedHashMap<>();
        for (String key : orderedKeys) {
            String[] parts = key.split("\\.");
            if (parts.length == 1) {
                // Top-level key
                tempMap.put(key, config.get(key));
            } else {
                // Nested key
                String topKey = parts[0];
                Config sub = config.get(topKey);
                if (sub instanceof Config) {
                    // Collects relevant nested keys
                    List<String> subKeys = orderedKeys.stream()
                            .filter(subKey -> subKey.startsWith(topKey + "."))
                            .map(subKey -> subKey.substring(topKey.length() + 1)) // Removes the prefix
                            .toList();
                    // Recursively reorders the sub-table
                    this.reorderTable(sub, subKeys);
                    if (!tempMap.containsKey(topKey))
                        tempMap.put(topKey, sub);
                }
            }
        }
        // Clears the config, and reinserts the ordered entries from tempMap
        config.clear();
        tempMap.forEach(config::set);
    }

    /**
     * Adds comments specified by {@link PaCoConfigValues.Comment} to the config entries, this also includes ranges.
     * <br/>
     * <strong>Note</strong>: this method doesn't perform any validation, so it should only be
     * used if the config has been validated and the entries in {@link PaCoConfigManager#config}
     * match the fields specified in {@link PaCoConfig}.
     */
    private void setConfigComments() {
        for (ConfigDataHolder holder : this.annotationHandler.getConfigDataHolders())
            if (holder.hasComment())
                this.config.setComment(holder.getPath(), holder.getComment());
    }

    /**
     * Modifies the in memory field values inside the {@link PaCoConfig} class.
     * <br/>
     * <strong>Note</strong>: this method doesn't perform any validation, so it should only be
     * used if the config has been validated and the entries in {@link PaCoConfigManager#config}
     * match the fields specified in {@link PaCoConfig}.
     */
    public void updateConfigFields() {
        for (ConfigDataHolder holder : this.annotationHandler.getConfigDataHolders()) {
            // Skips over holders that don't have a field e.g. categories.
            if (!holder.hasField())
                continue;
            ConfigDataHolderEntry holderEntry = (ConfigDataHolderEntry) holder;
            Field field = holderEntry.getField();
            field.setAccessible(true);
            try {
                field.set(null, holderEntry.convert(this.getConfig().get(holderEntry.getPath())));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set value for field: " + field.getName(), e);
            }
        }
    }

    /** Runnable that gets called when the config is automatically re-loaded. */
    private Runnable autoReloadListener() {
        return this::correctIfNeeded;
    }

    // These may still get replaced or modified, it all depends on how I decide to deal with registration...
    //##################################################################################################################
    public static void register(Class<?> configClass) {
        PaCoConfig.Config configAnnotation = configClass.getAnnotation(PaCoConfig.Config.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + configClass.getName() + " must be annotated with @PaCoConfig.Config");
        if (CONFIG_MANAGERS.containsKey(configClass))
            throw new IllegalStateException("Config class " + configClass.getName() + " is already registered!");
        PaCoConfigManager manager =  new PaCoConfigManager(configClass);
        CONFIG_MANAGERS.put(configClass, manager);
    }

    public static PaCoConfigManager getPaCoConfigmanager(Class<?> configClass) {
        return CONFIG_MANAGERS.get(configClass);
    }

    public static Collection<PaCoConfigManager> getPaCoConfigManagers() {
        return CONFIG_MANAGERS.values();
    }
    //##################################################################################################################



















//    /**
//     * Loads the config or creates the file if it doesn't exist.
//     */
//    public void loadOrCreateConfig() {
//        this.config.load();
//
//        if (this.config.isEmpty()) {
//            // If the file is empty or missing, we populate it with the default values
//            LOGGER.info("Config file is missing or empty. Writing default values.");
//            this.annotationHandler.writeDefaultValues(this.configInstance, this.config);
//            this.config.save();
//        } else {
//            // If the file exists and has values, we load them into the config class
//            LOGGER.info("Config file found. Loading values.");
//            this.annotationHandler.loadConfigValues(this.configInstance, this.config);
//        }
//    }
    private static class CorrectionEntry {
        String key;
        String detected;
        String corrected;

        CorrectionEntry(String key, Object detected, Object corrected) {
            this.key = key;
            this.detected = detected != null ? detected.toString() : "null";
            this.corrected = corrected != null ? corrected.toString() : "null";
        }
    }
}
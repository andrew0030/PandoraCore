package com.github.andrew0030.pandora_core.config.manager;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

public class PaCoConfigManager {

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoConfigManager");
    // A Map to store PaCoConfigManager instances
    private static final Map<String, PaCoConfigManager> CONFIG_MANAGERS = new HashMap<>();
    // Config Managing
    private final Object configInstance;               // The config instance with the annotated fields
    private final AnnotationHandler annotationHandler; // Helper class that deals with annotations
    private final CommentedFileConfig config;          // The config file

    private PaCoConfigManager(Object configInstance) {
        this.configInstance = configInstance;
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
        Path configDirectory = Services.PLATFORM.getConfigDirectory();
        Path configFilePath = configDirectory.resolve(configName + ".toml");
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
            // Saves the corrected and ordered config to the file, as the corrections need to be applied there as well
            this.config.save();
        } else {
            // Orders config, this is needed because the config from .load() is unordered
            // We don't need to call .save(), because in this case the file itself was correct,
            // and we simply order the "in memory" config for QoL
            this.orderConfigEntries();
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

    /** @return the config instance that was used to register this manager. */
    public Object getConfigInstance() {
        return this.configInstance;
    }

    /** @return the <code>.class</code> of the config instance that was used to register this manager. */
    public Class<?> getConfigClass() {
        return this.configInstance.getClass();
    }

    public void closeConfig() {
        this.config.close();
    }

    /**
     * Re-orders the config entries, to match the annotation order of the given {@link PaCoConfig}.
     * <br/>
     * <strong>Note</strong>: this method doesn't perform any validation, so it should only be
     * used if the config has been validated and the entries in {@link PaCoConfigManager#config}
     * match the fields specified in {@link PaCoConfig}.
     */
    private void orderConfigEntries() {
        Map<String, Object> tempMap = new LinkedHashMap<>();
        // Iterates over the ordered keys and retrieves their values, before storing them in tempMap
        for (String key : this.annotationHandler.getAnnotatedFields()) {
            Object value = this.config.get(key);
            tempMap.put(key, value);
        }
        // Clears the original config, and reinserts the ordered entries from tempMap
        this.config.clear();
        tempMap.forEach(this.config::set);
    }

    /** Runnable that gets called when the config is automatically re-loaded. */
    private Runnable autoReloadListener() {
        return this::correctIfNeeded;
    }

    // These may still get replaced or modified, it all depends on how I decide to deal with registration...
    //##################################################################################################################
    public static void register(Object configInstance) {
        PaCoConfig configAnnotation = configInstance.getClass().getAnnotation(PaCoConfig.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + configInstance.getClass().getName() + " must be annotated with @PaCoConfig");
        PaCoConfigManager manager =  new PaCoConfigManager(configInstance);
        CONFIG_MANAGERS.put(configInstance.getClass().getName(), manager);
    }

    public static PaCoConfigManager getPaCoConfigmanager(Object configInstance) {
        return CONFIG_MANAGERS.get(configInstance.getClass().getName());
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
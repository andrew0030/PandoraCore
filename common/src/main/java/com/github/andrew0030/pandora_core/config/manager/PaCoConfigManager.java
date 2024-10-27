package com.github.andrew0030.pandora_core.config.manager;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
        this.config.load();
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
        return CommentedFileConfig.builder(configFilePath).preserveInsertionOrder().autoreload().onLoad(this.loadListener()).build();
    }

    private Runnable loadListener() {
        return this::correctIfNeeded;
    }

    private void correctIfNeeded() {
        ConfigSpec configSpec = this.annotationHandler.getConfigSpec();
        boolean isConfigCorrect = configSpec.isCorrect(this.config);
        // If the config isn't correct we handle it.
        if (!isConfigCorrect) {
            // Listener to log corrections made to the config
            ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
                String pathString = String.join(".", path);
                LOGGER.warn(" - Config Correction | Key: {} | Detected: {} | Corrected: {}", pathString, incorrectValue, correctedValue);
            };
            LOGGER.warn("Detected inconsistencies in [{}] config. Initiating corrections...", this.annotationHandler.getConfigName());
            int correctionCount = configSpec.correct(this.config, listener);
            LOGGER.info("Correction Summary for [{}]: {} values adjusted.", this.annotationHandler.getConfigName(), correctionCount);
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

    public Object getConfigInstance() {
        return this.configInstance;
    }

    public Class<?> getConfigClass() {
        return this.configInstance.getClass();
    }

    public void closeConfig() {
        LOGGER.info("Closing [{}] config", this.annotationHandler.getConfigName());
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
}
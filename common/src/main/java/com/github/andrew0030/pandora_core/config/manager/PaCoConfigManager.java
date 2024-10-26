package com.github.andrew0030.pandora_core.config.manager;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
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
    private final ConfigSpec configSpec;               // A config spec used for validation/correction
    private final CommentedFileConfig config;          // The config file

    private PaCoConfigManager(Object configInstance) {
        this.configInstance = configInstance;
        this.annotationHandler = new AnnotationHandler(this);
        this.configSpec = this.annotationHandler.createConfigSpec();
        this.config = this.createConfig();

        this.loadAndCorrect();
    }

    /**
     * Creates a {@link CommentedFileConfig} instance for the configuration file
     * based on the name specified in the {@link PaCoConfig} annotation.<br/>
     * Note: At this point the config is still "empty" and {@link CommentedFileConfig#load()}
     * still needs to be called to load the config from the file.
     *
     * @return a {@link CommentedFileConfig} instance
     */
    private CommentedFileConfig createConfig() {
        String configName = this.annotationHandler.getConfigName();
        Path configDirectory = Services.PLATFORM.getConfigDirectory();
        Path configFilePath = configDirectory.resolve(configName + ".toml");
        // Creates the CommentedFileConfig instance
        return CommentedFileConfig.builder(configFilePath).preserveInsertionOrder().build();
    }

    private void loadAndCorrect() {
        this.config.load(); // Loads the config from the file
        boolean isConfigCorrect = this.configSpec.isCorrect(this.config);
        // If the config isn't correct we handle it.
        if (!isConfigCorrect) {
            // Listener to log corrections made to the config
            ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
                String pathString = String.join(".", path);
                LOGGER.warn(" - Config Correction | Key: {} | Detected: {} | Corrected: {}", pathString, incorrectValue, correctedValue);
            };
            LOGGER.warn("Detected inconsistencies in [{}] config. Initiating corrections...", this.annotationHandler.getConfigName());
            int correctionCount = this.configSpec.correct(this.config, listener);
            LOGGER.info("Correction Summary for [{}]: {} values adjusted.", this.annotationHandler.getConfigName(), correctionCount);
        }

        //TODO Remove later
        Map<String, Object> tempMap = new LinkedHashMap<>();
        // Iterate over the ordered keys and retrieve their values
        for (String key : Arrays.stream(this.configInstance.getClass().getDeclaredFields()).map(Field::getName).toList()) {
            Object value = this.config.get(key); // Get the corrected value
            tempMap.put(key, value); // Store in the temp map
        }
        // Clear the original config and reinsert in order
        this.config.clear(); // Clear existing values
        tempMap.forEach(this.config::set); // Reinsert the values in order


        System.out.println(this.config.entrySet().stream().map(UnmodifiableConfig.Entry::getKey).toList());

        this.config.setComment("falseValue", """
                 This is a comment block test.
                 Is this line in the next line?
                 Range [0 - 10] (not really just comment testing)
                """);
        this.config.save();
    }

    public Object getConfigInstance() {
        return this.configInstance;
    }

    public Class<?> getConfigClass() {
        return this.configInstance.getClass();
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
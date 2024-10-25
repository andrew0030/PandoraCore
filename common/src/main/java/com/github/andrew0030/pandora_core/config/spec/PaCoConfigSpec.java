package com.github.andrew0030.pandora_core.config.spec;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.AnnotationHandler;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.nio.file.Path;

public class PaCoConfigSpec {

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "PaCoConfigSpec");
    // Helper class that deals with annotations
    private final AnnotationHandler annotationHandler;
    // Config Managing
    private final ConfigSpec configSpec;
    private final CommentedFileConfig config;
    private final Object configInstance;

    private PaCoConfigSpec(Object configInstance) {
        this.annotationHandler = new AnnotationHandler(configInstance.getClass());
        this.configSpec = new ConfigSpec();
        this.config = this.createConfig();
        this.configInstance = configInstance;

        this.loadOrCreateConfig();
    }

    public static void register(Object configInstance) {
        new PaCoConfigSpec(configInstance);
    }

    /**
     * Creates a {@link CommentedFileConfig} instance for the configuration file
     * based on the name specified in the {@link PaCoConfig} annotation.
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

    //TODO replace this entire thing with a config spec setup to validate the file and to correct the values
    /**
     * Loads the config or creates the file if it doesn't exist.
     */
    public void loadOrCreateConfig() {
        this.config.load();

        if (this.config.isEmpty()) {
            // If the file is empty or missing, we populate it with the default values
            LOGGER.info("Config file is missing or empty. Writing default values.");
            this.annotationHandler.writeDefaultValues(this.configInstance, this.config);
            this.config.save();
        } else {
            // If the file exists and has values, we load them into the config class
            LOGGER.info("Config file found. Loading values.");
            this.annotationHandler.loadConfigValues(this.configInstance, this.config);
        }
    }
}
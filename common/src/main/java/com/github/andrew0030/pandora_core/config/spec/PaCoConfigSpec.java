package com.github.andrew0030.pandora_core.config.spec;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
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
    private final FileConfig fileConfig;
    private final Object configInstance;

    private PaCoConfigSpec(Object configInstance) {
        this.annotationHandler = new AnnotationHandler(configInstance.getClass());
        this.configSpec = new ConfigSpec();
        this.fileConfig = this.createFileConfig();
        this.configInstance = configInstance;

        this.loadOrCreateConfig();
    }

    public static void register(Object configInstance) {
        new PaCoConfigSpec(configInstance);
    }

    /**
     * Creates a {@link FileConfig} instance for the configuration file
     * based on the name specified in the {@link PaCoConfig} annotation.
     *
     * @return a {@link FileConfig} instance
     */
    private FileConfig createFileConfig() {
        String configName = this.annotationHandler.getConfigName();
        Path configDirectory = Services.PLATFORM.getConfigDirectory();
        Path configFilePath = configDirectory.resolve(configName + ".toml");
        // Creates the FileConfig instance
        return FileConfig.builder(configFilePath).build();
    }

    //TODO replace this entire thing with a config spec setup to validate the file and to correct the values
    /**
     * Loads the config or creates the file if it doesn't exist.
     */
    public void loadOrCreateConfig() {
        this.fileConfig.load();

        if (this.fileConfig.isEmpty()) {
            // If the file is empty or missing, we populate it with the default values
            LOGGER.info("Config file is missing or empty. Writing default values.");
            this.annotationHandler.writeDefaultValues(this.configInstance, this.fileConfig);
            this.fileConfig.save();
        } else {
            // If the file exists and has values, we load them into the config class
            LOGGER.info("Config file found. Loading values.");
            this.annotationHandler.loadConfigValues(this.configInstance, this.fileConfig);
        }
    }
}
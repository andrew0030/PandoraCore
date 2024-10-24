package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final Class<?> configClass;
    private final String configName;

    public AnnotationHandler(Class<?> configClass) {
        this.configClass = configClass;
        this.configName = this.retrieveConfigName();
    }

    private String retrieveConfigName() {
        PaCoConfig configAnnotation = configClass.getAnnotation(PaCoConfig.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + configClass.getName() + " must be annotated with @PaCoConfig");
        return String.format("%s-%s", configAnnotation.modId(), configAnnotation.name());
    }

    /** @return The name specified in the {@link PaCoConfig} annotation. */
    public String getConfigName() {
        return configName;
    }

    /**
     * Writes default values from the config instance to the {@link FileConfig}
     */
    public void writeDefaultValues(Object configInstance, FileConfig fileConfig) {
        for (Field field : configClass.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
                    field.setAccessible(true);
                    int value = field.getInt(configInstance);
                    fileConfig.set(field.getName(), value);
                    LOGGER.info("Writing default value for field '{}': {}", field.getName(), value);
                }



            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to set field '{}'", field.getName(), e);
            }
        }
    }

    /**
     * Loads values from the file and sets them on the config class fields.
     */
    public void loadConfigValues(Object configInstance, FileConfig fileConfig) {
        for (Field field : configClass.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
                    field.setAccessible(true);
                    int defaultValue = field.getInt(configInstance);
                    int loadedValue = fileConfig.getOrElse(field.getName(), defaultValue);
                    field.setInt(configInstance, loadedValue); // Sets the loaded value back to the field
                    LOGGER.info("Loaded value for field '{}': {}", field.getName(), loadedValue);
                }



            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to set field '{}'", field.getName(), e);
            }
        }
    }
}
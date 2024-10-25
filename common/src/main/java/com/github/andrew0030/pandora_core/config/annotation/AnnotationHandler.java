package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final PaCoConfigManager manager;
    private final String configName;

    public AnnotationHandler(PaCoConfigManager manager) {
        this.manager = manager;
        this.configName = this.retrieveConfigName();
    }

    private String retrieveConfigName() {
        PaCoConfig configAnnotation = this.manager.getConfigClass().getAnnotation(PaCoConfig.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + this.manager.getConfigClass().getName() + " must be annotated with @PaCoConfig");
        return String.format("%s-%s", configAnnotation.modId(), configAnnotation.name());
    }

    public ConfigSpec createConfigSpec() {
        ConfigSpec configSpec = new ConfigSpec();
        for (Field field : this.manager.getConfigClass().getDeclaredFields()) {
            // Boolean
            if (field.isAnnotationPresent(PaCoConfigValues.BooleanValue.class)) {
                if (field.getType() != boolean.class)
                    throw new IllegalArgumentException("Field: '" + field.getName() + "' in Class: '" + this.manager.getConfigClass().getName() + "' must be of type boolean for BooleanValue annotation.");
                field.setAccessible(true);
                try {
                    boolean defaultValue = field.getBoolean(this.manager.getConfigInstance());
                    configSpec.define(field.getName(), defaultValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            // Integer
            if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
                if (field.getType() != int.class)
                    throw new IllegalArgumentException("Field: '" + field.getName() + "' in Class: '" + this.manager.getConfigClass().getName() + "' must be of type int for IntegerValue annotation.");
                field.setAccessible(true);
                try {
                    int defaultValue = field.getInt(this.manager.getConfigInstance());
                    configSpec.define(field.getName(), defaultValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return configSpec;
    }

    /**
     * Gets the name the config should have, this happens by checking {@link PaCoConfig}
     * and using the {@link PaCoConfig#modId()} and {@link PaCoConfig#name()} to create
     * a {@link String} with the format of:<br/>
     * <code>example_id-some_name</code><br/>
     * <strong>Note</strong>: This does not contain the <strong>file type</strong> (<code>.toml</code>)
     *
     * @return The name specified in the {@link PaCoConfig} annotation.
     */
    public String getConfigName() {
        return this.configName;
    }












//    /**
//     * Writes default values from the config instance to the {@link CommentedFileConfig}
//     */
//    public void writeDefaultValues(Object configInstance, CommentedFileConfig config) {
//        for (Field field : this.configClass.getDeclaredFields()) {
//            try {
//                if (field.isAnnotationPresent(PaCoConfigValues.BooleanValue.class)) {
//                    field.setAccessible(true);
//                    boolean value = field.getBoolean(configInstance);
//                    config.add(field.getName(), value);
//                    LOGGER.info("Writing default value for field '{}': {}", field.getName(), value);
//                }
//
//                if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
//                    field.setAccessible(true);
//                    int value = field.getInt(configInstance);
//                    config.add(field.getName(), value);
//                    LOGGER.info("Writing default value for field '{}': {}", field.getName(), value);
//                }
//
//
//
//            } catch (IllegalAccessException e) {
//                LOGGER.error("Failed to set field '{}'", field.getName(), e);
//            }
//        }
//    }
//
//    /**
//     * Loads values from the file and sets them on the config class fields.
//     */
//    public void loadConfigValues(Object configInstance, CommentedFileConfig config) {
//        for (Field field : this.configClass.getDeclaredFields()) {
//            try {
//                if (field.isAnnotationPresent(PaCoConfigValues.BooleanValue.class)) {
//                    field.setAccessible(true);
//                    boolean defaultValue = field.getBoolean(configInstance);
//                    boolean loadedValue = config.getOrElse(field.getName(), defaultValue);
//                    field.setBoolean(configInstance, loadedValue); // Sets the loaded value back to the field
//                    LOGGER.info("Loaded value for field '{}': {}", field.getName(), loadedValue);
//                }
//
//                if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
//                    field.setAccessible(true);
//                    int defaultValue = field.getInt(configInstance);
//                    int loadedValue = config.getOrElse(field.getName(), defaultValue);
//                    field.setInt(configInstance, loadedValue); // Sets the loaded value back to the field
//                    LOGGER.info("Loaded value for field '{}': {}", field.getName(), loadedValue);
//                }
//
//
//
//            } catch (IllegalAccessException e) {
//                LOGGER.error("Failed to set field '{}'", field.getName(), e);
//            }
//        }
//    }
}
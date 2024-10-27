package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final List<String> annotatedFields = new ArrayList<>();
    private final ConfigSpec configSpec = new ConfigSpec();
    private final PaCoConfigManager manager;
    private final String configName;

    public AnnotationHandler(PaCoConfigManager manager) {
        this.manager = manager;
        this.configName = this.initConfigName();
        // Initializes the: annotatedFields, configSpec
        this.initConfigCaches();
    }

    /** Initializes the config name */
    private String initConfigName() {
        PaCoConfig configAnnotation = this.manager.getConfigClass().getAnnotation(PaCoConfig.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + this.manager.getConfigClass().getName() + " must be annotated with @PaCoConfig");
        return String.format("%s-%s", configAnnotation.modId(), configAnnotation.name());
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

    /**
     * TODO: finish javadoc
     * <br/>
     * <strong>Note</strong>: This method ensures type safety.
     */
    private void initConfigCaches() {
        for (Field field : this.manager.getConfigClass().getDeclaredFields()) {
            // Boolean
            if (field.isAnnotationPresent(PaCoConfigValues.BooleanValue.class)) {
                if (field.getType() != boolean.class)
                    throw new IllegalArgumentException("Field: '" + field.getName() + "' in Class: '" + this.manager.getConfigClass().getName() + "' must be of type boolean for BooleanValue annotation.");
                field.setAccessible(true);
                try {
                    boolean defaultValue = field.getBoolean(this.manager.getConfigInstance());
                    configSpec.define(field.getName(), defaultValue);
                    this.annotatedFields.add(field.getName());
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
                    this.annotatedFields.add(field.getName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Used to retrieve a fully defined {@link ConfigSpec}, containing all the annotated fields as entries.
     *
     * @return the {@link ConfigSpec} created based on the specified {@link PaCoConfig}
     */
    public ConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    /**
     * TODO: finish javadoc
     * @return
     */
    public ImmutableList<String> getAnnotatedFields() {
        return ImmutableList.copyOf(this.annotatedFields);
    }

//    public ConfigSpec createConfigSpec() {
//        ConfigSpec configSpec = new ConfigSpec();
//        for (Field field : this.manager.getConfigClass().getDeclaredFields()) {
//            // Boolean
//            if (field.isAnnotationPresent(PaCoConfigValues.BooleanValue.class)) {
//                if (field.getType() != boolean.class)
//                    throw new IllegalArgumentException("Field: '" + field.getName() + "' in Class: '" + this.manager.getConfigClass().getName() + "' must be of type boolean for BooleanValue annotation.");
//                field.setAccessible(true);
//                try {
//                    boolean defaultValue = field.getBoolean(this.manager.getConfigInstance());
//                    configSpec.define(field.getName(), defaultValue);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            // Integer
//            if (field.isAnnotationPresent(PaCoConfigValues.IntegerValue.class)) {
//                if (field.getType() != int.class)
//                    throw new IllegalArgumentException("Field: '" + field.getName() + "' in Class: '" + this.manager.getConfigClass().getName() + "' must be of type int for IntegerValue annotation.");
//                field.setAccessible(true);
//                try {
//                    int defaultValue = field.getInt(this.manager.getConfigInstance());
//                    configSpec.define(field.getName(), defaultValue);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        return configSpec;
//    }
}
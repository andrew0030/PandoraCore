package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final Map<Class<? extends Annotation>, Consumer<Field>> annotationHandlers = new HashMap<>();
    private final Map<String, ConfigDataHolder> dataHolders = new LinkedHashMap<>();
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
        this.annotationHandlers.put(PaCoConfigValues.BooleanValue.class, this::handleBooleanField);
        this.annotationHandlers.put(PaCoConfigValues.IntegerValue.class, this::handleIntegerField);
        this.annotationHandlers.put(PaCoConfigValues.DoubleValue.class, this::handleDoubleField);
        this.annotationHandlers.put(PaCoConfigValues.FloatValue.class, this::handleFloatField);
        this.annotationHandlers.put(PaCoConfigValues.LongValue.class, this::handleLongField);
        this.annotationHandlers.put(PaCoConfigValues.StringValue.class, this::handleStringField);
        this.annotationHandlers.put(PaCoConfigValues.ListValue.class, this::handleListField);
        this.annotationHandlers.put(PaCoConfigValues.EnumValue.class, this::handleEnumField);
        this.annotationHandlers.put(PaCoConfigValues.Comment.class, this::handleComment);

        for (Field field : this.manager.getConfigClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Consumer<Field> consumer = this.annotationHandlers.get(annotation.annotationType());
                if (consumer != null)
                    consumer.accept(field);
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
        return ImmutableList.copyOf(this.dataHolders.keySet());
    }

    public ImmutableList<ConfigDataHolder> getConfigDataHolders() {
        return ImmutableList.copyOf(this.dataHolders.values());
    }

    // ######################################################################
    // #                           Field Handling                           #
    // ######################################################################

    private void handleBooleanField(Field field) {
        if (field.getType() != boolean.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type boolean for BooleanValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        field.setAccessible(true);
        try {
            boolean defaultValue = field.getBoolean(this.manager.getConfigInstance());
            configSpec.define(field.getName(), defaultValue);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIntegerField(Field field) {
        if (field.getType() != int.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type int for IntegerValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        PaCoConfigValues.IntegerValue integerAnnotation = field.getAnnotation(PaCoConfigValues.IntegerValue.class);
        field.setAccessible(true);
        try {
            int defaultValue = field.getInt(this.manager.getConfigInstance());
            int minVal = integerAnnotation.minValue();
            int maxVal = integerAnnotation.maxValue();
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %d is out of range (min: %d, max: %d).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            configSpec.defineInRange(field.getName(), defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Integer.MIN_VALUE ? null : minVal, maxVal == Integer.MAX_VALUE ? null : maxVal));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDoubleField(Field field) {
        if (field.getType() != double.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type double for DoubleValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        PaCoConfigValues.DoubleValue doubleAnnotation = field.getAnnotation(PaCoConfigValues.DoubleValue.class);
        field.setAccessible(true);
        try {
            double defaultValue = field.getDouble(this.manager.getConfigInstance());
            double minVal = doubleAnnotation.minValue();
            double maxVal = doubleAnnotation.maxValue();
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %f is out of range (min: %f, max: %f).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            configSpec.defineInRange(field.getName(), defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Double.MIN_VALUE ? null : minVal, maxVal == Double.MAX_VALUE ? null : maxVal));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFloatField(Field field) {
        if (field.getType() != float.class) //TODO maybe add 'Float' support?
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type float for FloatValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        PaCoConfigValues.FloatValue floatAnnotation = field.getAnnotation(PaCoConfigValues.FloatValue.class);
        field.setAccessible(true);
        try {
            float defaultValue = field.getFloat(this.manager.getConfigInstance());
            float minVal = floatAnnotation.minValue();
            float maxVal = floatAnnotation.maxValue();
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %f is out of range (min: %f, max: %f).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            configSpec.defineInRange(field.getName(), defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Float.MIN_VALUE ? null : minVal, maxVal == Float.MAX_VALUE ? null : maxVal).setConverter(value -> {
                if (value instanceof Number number)
                    return number.floatValue();
                throw new IllegalArgumentException("Config value is not a Number as expected for float.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLongField(Field field) {
        if (field.getType() != long.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type long for LongValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        PaCoConfigValues.LongValue longAnnotation = field.getAnnotation(PaCoConfigValues.LongValue.class);
        field.setAccessible(true);
        try {
            Long defaultValue = field.getLong(this.manager.getConfigInstance());
            Long minVal = longAnnotation.minValue();
            Long maxVal = longAnnotation.maxValue();
            if (minVal.compareTo(maxVal) > 0) {
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': The minimum must be less than the maximum.",
                        field.getName(),
                        this.manager.getConfigClass().getName()
                ));
            }
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %d is out of range (min: %d, max: %d).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            configSpec.define(field.getName(), defaultValue, o -> {
                if (o instanceof Integer || o instanceof Long) {
                    long longValue = ((Number) o).longValue();
                    return longValue >= minVal && longValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(),holder.setRange(minVal == Long.MIN_VALUE ? null : minVal, maxVal == Long.MAX_VALUE ? null : maxVal));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStringField(Field field) {
        if (field.getType() != String.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type String for StringValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        field.setAccessible(true);
        try {
            String defaultValue = (String) field.get(this.manager.getConfigInstance());
            configSpec.define(field.getName(), defaultValue);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleListField(Field field) {
        if (field.getType() != List.class)
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type List for ListValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        PaCoConfigValues.ListValue listAnnotation = field.getAnnotation(PaCoConfigValues.ListValue.class);
        field.setAccessible(true);
        try {
            List<?> defaultValue = (List<?>) field.get(this.manager.getConfigInstance());
            if (defaultValue == null)
                throw new IllegalArgumentException(String.format(
                        "Field: '%s' in Class: '%s' cannot have a null list as a default value.",
                        field.getName(),
                        this.manager.getConfigClass().getName()
                ));
            configSpec.defineList(field.getName(), defaultValue, element -> listAnnotation.elementType().isInstance(element));
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEnumField(Field field) {
        if (!field.getType().isEnum())
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type Enum for EnumValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        field.setAccessible(true);
        try {
            Enum<?> defaultValue = (Enum<?>) field.get(this.manager.getConfigInstance());
            Class<? extends Enum> enumClass = (Class<? extends Enum>) field.getType();
            Object[] enumConstants = enumClass.getEnumConstants();
            List<String> enumNames = Arrays.stream(enumConstants)
                    .map(enumConstant -> ((Enum<?>) enumConstant).name())
                    .toList();
            configSpec.define(field.getName(), defaultValue.name(), value -> {
                if (value instanceof String stringVal) {
                    try {
                        Enum.valueOf(enumClass, stringVal);
                        return true;
                    } catch (IllegalArgumentException ignored) {}
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setValidValues(enumNames).setConverter(value -> {
                if (value instanceof String stringVal)
                    return Enum.valueOf(enumClass, stringVal);
                throw new IllegalArgumentException("Config value is not a String as expected for enum.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleComment(Field field) {
        PaCoConfigValues.Comment commentAnnotation = field.getAnnotation(PaCoConfigValues.Comment.class);
        ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
        if (holder != null)
            holder.setComment(commentAnnotation.value(), commentAnnotation.padding());
    }
}
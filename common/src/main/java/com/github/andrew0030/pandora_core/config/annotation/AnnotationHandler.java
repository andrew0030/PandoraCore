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
import java.util.stream.Collectors;

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
        PaCoConfig.Config configAnnotation = this.manager.getConfigClass().getAnnotation(PaCoConfig.Config.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + this.manager.getConfigClass().getName() + " must be annotated with @PaCoConfig.Config");
        return String.format("%s-%s", configAnnotation.modId(), configAnnotation.name());
    }

    /**
     * Gets the name the config should have, this happens by checking {@link PaCoConfig.Config}
     * and using the {@link PaCoConfig.Config#modId()} and {@link PaCoConfig.Config#name()} to create
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
     * This method initializes the annotation handler caches.
     * <br/>
     * <strong>Note</strong>: This method ensures type safety.
     */
    private void initConfigCaches() {
        this.annotationHandlers.put(PaCoConfigValues.BooleanValue.class, this::handleBooleanField);
        this.annotationHandlers.put(PaCoConfigValues.IntegerValue.class, this::handleIntegerField);
        this.annotationHandlers.put(PaCoConfigValues.ByteValue.class, this::handleByteField);
        this.annotationHandlers.put(PaCoConfigValues.ShortValue.class, this::handleShortField);
        this.annotationHandlers.put(PaCoConfigValues.DoubleValue.class, this::handleDoubleField);
        this.annotationHandlers.put(PaCoConfigValues.FloatValue.class, this::handleFloatField);
        this.annotationHandlers.put(PaCoConfigValues.LongValue.class, this::handleLongField);
        this.annotationHandlers.put(PaCoConfigValues.StringValue.class, this::handleStringField);
        this.annotationHandlers.put(PaCoConfigValues.ListValue.class, this::handleListField);
        this.annotationHandlers.put(PaCoConfigValues.EnumValue.class, this::handleEnumField);
        this.annotationHandlers.put(PaCoConfigValues.Comment.class, this::handleComment);
        this.annotationHandlers.put(PaCoConfigValues.Category.class, this::handleCategory);

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
     * @return the {@link ConfigSpec} created based on the specified {@link PaCoConfig}
     */
    public ConfigSpec getConfigSpec() {
        return this.configSpec;
    }

    /** @return an ordered {@link ImmutableList}, containing a {@link String} for each field name inside the {@link PaCoConfig} class */
    public ImmutableList<String> getAnnotatedFields() {
        return ImmutableList.copyOf(this.dataHolders.keySet());
    }

    /** @return an ordered {@link ImmutableList}, containing a {@link ConfigDataHolder} for each field inside the {@link PaCoConfig} class */
    public ImmutableList<ConfigDataHolder> getConfigDataHolders() {
        return ImmutableList.copyOf(this.dataHolders.values());
    }

    // ######################################################################
    // #                           Field Handling                           #
    // ######################################################################

    private void handleBooleanField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.BooleanValue.class.getSimpleName(), boolean.class, Boolean.class);
        field.setAccessible(true);
        try {
            boolean defaultValue = (boolean) field.get(this.manager.getConfigInstance());
            configSpec.define(field.getName(), defaultValue);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIntegerField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.IntegerValue.class.getSimpleName(), int.class, Integer.class);
        PaCoConfigValues.IntegerValue integerAnnotation = field.getAnnotation(PaCoConfigValues.IntegerValue.class);
        field.setAccessible(true);
        try {
            int defaultValue = (int) field.get(this.manager.getConfigInstance());
            int minVal = integerAnnotation.minValue();
            int maxVal = integerAnnotation.maxValue();
            boolean showFullRange = integerAnnotation.showFullRange();
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
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Integer.MIN_VALUE ? null : minVal, maxVal == Integer.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleByteField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.ByteValue.class.getSimpleName(), byte.class, Byte.class);
        PaCoConfigValues.ByteValue byteAnnotation = field.getAnnotation(PaCoConfigValues.ByteValue.class);
        field.setAccessible(true);
        try {
            byte defaultValue = (byte) field.get(this.manager.getConfigInstance());
            byte minVal = byteAnnotation.minValue();
            byte maxVal = byteAnnotation.maxValue();
            boolean showFullRange = byteAnnotation.showFullRange();
            if (minVal >= maxVal) {
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
                if (o instanceof Integer || o instanceof Byte) {
                    byte byteValue = ((Number) o).byteValue();
                    return byteValue >= minVal && byteValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Byte.MIN_VALUE ? null : minVal, maxVal == Byte.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal).setConverter(value -> {
                if (value instanceof Number number)
                    return number.byteValue();
                throw new IllegalArgumentException("Config value is not a Number as expected for byte.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleShortField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.ShortValue.class.getSimpleName(), short.class, Short.class);
        PaCoConfigValues.ShortValue shortAnnotation = field.getAnnotation(PaCoConfigValues.ShortValue.class);
        field.setAccessible(true);
        try {
            short defaultValue = (short) field.get(this.manager.getConfigInstance());
            short minVal = shortAnnotation.minValue();
            short maxVal = shortAnnotation.maxValue();
            boolean showFullRange = shortAnnotation.showFullRange();
            if (minVal >= maxVal) {
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
                if (o instanceof Integer || o instanceof Short) {
                    short shortValue = ((Number) o).shortValue();
                    return shortValue >= minVal && shortValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(field.getName(), new ConfigDataHolder(field));
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Short.MIN_VALUE ? null : minVal, maxVal == Short.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal).setConverter(value -> {
                if (value instanceof Number number)
                    return number.shortValue();
                throw new IllegalArgumentException("Config value is not a Number as expected for short.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDoubleField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.DoubleValue.class.getSimpleName(), double.class, Double.class);
        PaCoConfigValues.DoubleValue doubleAnnotation = field.getAnnotation(PaCoConfigValues.DoubleValue.class);
        field.setAccessible(true);
        try {
            double defaultValue = (double) field.get(this.manager.getConfigInstance());
            double minVal = doubleAnnotation.minValue();
            double maxVal = doubleAnnotation.maxValue();
            boolean showFullRange = doubleAnnotation.showFullRange();
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
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Double.MIN_VALUE ? null : minVal, maxVal == Double.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFloatField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.FloatValue.class.getSimpleName(), float.class, Float.class);
        PaCoConfigValues.FloatValue floatAnnotation = field.getAnnotation(PaCoConfigValues.FloatValue.class);
        field.setAccessible(true);
        try {
            float defaultValue = (float) field.get(this.manager.getConfigInstance());
            float minVal = floatAnnotation.minValue();
            float maxVal = floatAnnotation.maxValue();
            boolean showFullRange = floatAnnotation.showFullRange();
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
            this.dataHolders.put(
                    field.getName(), holder.setRange(minVal == Float.MIN_VALUE ? null : minVal, maxVal == Float.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal).setConverter(value -> {
                if (value instanceof Number number)
                    return number.floatValue();
                throw new IllegalArgumentException("Config value is not a Number as expected for float.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLongField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.LongValue.class.getSimpleName(), long.class, Long.class);
        PaCoConfigValues.LongValue longAnnotation = field.getAnnotation(PaCoConfigValues.LongValue.class);
        field.setAccessible(true);
        try {
            long defaultValue = (long) field.get(this.manager.getConfigInstance());
            long minVal = longAnnotation.minValue();
            long maxVal = longAnnotation.maxValue();
            boolean showFullRange = longAnnotation.showFullRange();
            if (minVal >= maxVal) {
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
            this.dataHolders.put(field.getName(), holder.setRange(minVal == Long.MIN_VALUE ? null : minVal, maxVal == Long.MAX_VALUE ? null : maxVal).setShowFullRange(showFullRange, minVal, maxVal).setConverter(value -> {
                if (value instanceof Number number)
                    return number.longValue();
                throw new IllegalArgumentException("Config value is not a Number as expected for long.");
            }));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStringField(Field field) {
        this.checkFieldValidity(field, PaCoConfigValues.StringValue.class.getSimpleName(), String.class);
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
        this.checkFieldValidity(field, PaCoConfigValues.ListValue.class.getSimpleName(), List.class);
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
        this.dataHolders.put(field.getName(), holder.setComment(commentAnnotation.value(), commentAnnotation.padding()));
    }

    private void handleCategory(Field field) {
        field.setAccessible(true);
        try {
            // Gets or instantiates the category object
            Object categoryInstance = field.get(this.manager.getConfigInstance());
            if (categoryInstance == null) {
                categoryInstance = field.getType().getDeclaredConstructor().newInstance();
                field.set(this.manager.getConfigInstance(), categoryInstance);
            }

            PaCoConfig.Category categoryAnnotation = field.getType().getAnnotation(PaCoConfig.Category.class);
            String categoryName = categoryAnnotation.value();

            // TODO: replace this with a handler that passes on "categoryName" so subFields are properly categorized
            for (Field subField : field.getType().getDeclaredFields()) {
                for (Annotation annotation : subField.getAnnotations()) {
                    Consumer<Field> consumer = this.annotationHandlers.get(annotation.annotationType());
                    if (consumer != null)
                        consumer.accept(field);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to process category for field '%s'.", field.getName()), e);
        }
    }

    private void checkFieldValidity(Field field, String annotationName, Class<?>... types) {
        if (!Set.of(types).contains(field.getType()))
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type %s for '%s' annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName(),
                    Arrays.stream(types).map(clazz -> "'" + clazz.getSimpleName() + "'").collect(Collectors.joining(" or ")),
                    annotationName
            ));
    }
}
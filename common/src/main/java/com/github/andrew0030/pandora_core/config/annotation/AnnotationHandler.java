package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.config.manager.*;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private final Map<Class<? extends Annotation>, BiConsumer<Field, String>> annotationHandlers = new HashMap<>();
    private final Map<String, ConfigDataHolder> dataHolders = new LinkedHashMap<>();
    private final ConfigSpec configSpec = new ConfigSpec();
    private final PaCoConfigManager manager;
    private final String configName;
    private final String subFolder;

    public AnnotationHandler(PaCoConfigManager manager) {
        this.manager = manager;
        this.configName = this.initConfigName();
        this.subFolder = this.initConfigSubFolder();
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

    /** Retrieves and "normalizes" the config sub-folder, or returns an empty {@code String} if none was specified. */
    private String initConfigSubFolder() {
        PaCoConfig.SubFolder subFolderAnnotation = this.manager.getConfigClass().getAnnotation(PaCoConfig.SubFolder.class);
        // Returns early if no annotation was found
        if (subFolderAnnotation == null) return "";
        String subFolder = subFolderAnnotation.value();
        // Returns early if no String was given
        if (StringUtil.isNullOrEmpty(subFolder)) return "";
        // Normalizes and cleans up the given path (if needed)
        subFolder = subFolder.replace('\\', '/');    // Normalizes separators
        subFolder = subFolder.replaceAll("^/+", ""); // Removes leading slashes
        subFolder = subFolder.replaceAll("/+$", ""); // Removes trailing slashes
        subFolder = subFolder.replaceAll("/+", "/"); // Collapses multiple slashes

        return subFolder;
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
     * Gets the sub-folder the config should have, this happens by checking {@link PaCoConfig.SubFolder}
     * and using the specified {@link PaCoConfig.SubFolder#value()}. If no sub-folder is specified
     * an empty {@link String} is returned.
     *
     * @return The sub-folder specified in the {@link PaCoConfig} annotation.
     */
    public String getConfigSubFolder() {
        return this.subFolder;
    }

    /**
     * This method initializes the annotation handler caches.
     * <br/>
     * <strong>Note</strong>: This method ensures type safety.
     */
    private void initConfigCaches() {
        this.annotationHandlers.put(PaCoConfigValues.BooleanValue.class, this::handleBooleanField); // Boolean & boolean
        this.annotationHandlers.put(PaCoConfigValues.IntegerValue.class, this::handleIntegerField); // Integer & int
        this.annotationHandlers.put(PaCoConfigValues.ByteValue.class, this::handleByteField);       // Byte & byte
        this.annotationHandlers.put(PaCoConfigValues.ShortValue.class, this::handleShortField);     // Short & short
        this.annotationHandlers.put(PaCoConfigValues.DoubleValue.class, this::handleDoubleField);   // Double & double
        this.annotationHandlers.put(PaCoConfigValues.FloatValue.class, this::handleFloatField);     // Float & float
        this.annotationHandlers.put(PaCoConfigValues.LongValue.class, this::handleLongField);       // Long & long
        this.annotationHandlers.put(PaCoConfigValues.StringValue.class, this::handleStringField);   // String
        this.annotationHandlers.put(PaCoConfigValues.ListValue.class, this::handleListField);       // List
        this.annotationHandlers.put(PaCoConfigValues.EnumValue.class, this::handleEnumField);       // Enum
        this.annotationHandlers.put(PaCoConfigValues.CustomValue.class, this::handleCustomField);   // Custom Classes
        this.annotationHandlers.put(PaCoConfigValues.Comment.class, this::handleComment);           // Comments

        this.processConfigClass(this.manager.getConfigClass(), null);
    }

    /** Handles loading all the fields and subclasses inside the config */
    private void processConfigClass(Class<?> configClass, @Nullable String category) {
        String categoryPrefix = StringUtil.isNullOrEmpty(category) ? "" : category + ".";
        // Processes fields
        for (Field field : configClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (!Modifier.isStatic(field.getModifiers())) {
                throw new IllegalArgumentException(String.format(
                        "Field: '%s' in Class: '%s' must be a static field to be valid.",
                        field.getName(),
                        configClass.getName()
                ));
            }
            for (Annotation annotation : field.getAnnotations()) {
                BiConsumer<Field, String> consumer = this.annotationHandlers.get(annotation.annotationType());
                if (consumer != null)
                    consumer.accept(field, categoryPrefix);
            }
        }
        // Processes classes
        List<Class<?>> declaredClasses = Arrays.asList(configClass.getDeclaredClasses());
        Collections.reverse(declaredClasses);
        for (Class<?> clazz : declaredClasses) {
            if (clazz.isAnnotationPresent(PaCoConfig.Category.class)) {
                if (!clazz.isMemberClass() || !Modifier.isStatic(clazz.getModifiers())) {
                    throw new IllegalArgumentException(String.format(
                            "Class: '%s' in Class: '%s' must be a static inner class to be a valid category.",
                            clazz.getName(),
                            configClass.getName()
                    ));
                }
                String classCategory = categoryPrefix + clazz.getAnnotation(PaCoConfig.Category.class).value();
                if (clazz.isAnnotationPresent(PaCoConfig.Comment.class))
                    this.handleCategoryComment(clazz, classCategory);
                this.processConfigClass(clazz, classCategory);
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

    private void handleBooleanField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.BooleanValue.class.getSimpleName(), boolean.class, Boolean.class);
        field.setAccessible(true);
        try {
            boolean defaultValue = (boolean) this.getOrThrow(field);
            String key = category + field.getName();
            configSpec.define(key, defaultValue);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            this.dataHolders.put(key, holder.setPath(key));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIntegerField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.IntegerValue.class.getSimpleName(), int.class, Integer.class);
        PaCoConfigValues.IntegerValue integerAnnotation = field.getAnnotation(PaCoConfigValues.IntegerValue.class);
        field.setAccessible(true);
        try {
            int defaultValue = (int) this.getOrThrow(field);
            int minVal = integerAnnotation.minValue();
            int maxVal = integerAnnotation.maxValue();
            boolean showFullRange = integerAnnotation.showFullRange();
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
            String key = category + field.getName();
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Integer.MIN_VALUE ? null : minVal, maxVal == Integer.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleByteField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.ByteValue.class.getSimpleName(), byte.class, Byte.class);
        PaCoConfigValues.ByteValue byteAnnotation = field.getAnnotation(PaCoConfigValues.ByteValue.class);
        field.setAccessible(true);
        try {
            byte defaultValue = (byte) this.getOrThrow(field);
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
            String key = category + field.getName();
            configSpec.define(key, defaultValue, o -> {
                if (o instanceof Integer || o instanceof Byte) {
                    byte byteValue = ((Number) o).byteValue();
                    return byteValue >= minVal && byteValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Byte.MIN_VALUE ? null : minVal, maxVal == Byte.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setConverter(value -> {
                            if (value instanceof Number number)
                                return number.byteValue();
                            throw new IllegalArgumentException("Config value is not a Number as expected for byte.");
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleShortField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.ShortValue.class.getSimpleName(), short.class, Short.class);
        PaCoConfigValues.ShortValue shortAnnotation = field.getAnnotation(PaCoConfigValues.ShortValue.class);
        field.setAccessible(true);
        try {
            short defaultValue = (short) this.getOrThrow(field);
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
            String key = category + field.getName();
            configSpec.define(key, defaultValue, o -> {
                if (o instanceof Integer || o instanceof Short) {
                    short shortValue = ((Number) o).shortValue();
                    return shortValue >= minVal && shortValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Short.MIN_VALUE ? null : minVal, maxVal == Short.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setConverter(value -> {
                            if (value instanceof Number number)
                                return number.shortValue();
                            throw new IllegalArgumentException("Config value is not a Number as expected for short.");
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDoubleField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.DoubleValue.class.getSimpleName(), double.class, Double.class);
        PaCoConfigValues.DoubleValue doubleAnnotation = field.getAnnotation(PaCoConfigValues.DoubleValue.class);
        field.setAccessible(true);
        try {
            double defaultValue = (double) this.getOrThrow(field);
            double minVal = doubleAnnotation.minValue();
            double maxVal = doubleAnnotation.maxValue();
            boolean showFullRange = doubleAnnotation.showFullRange();
            if (minVal >= maxVal) {
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': The minimum must be less than the maximum.",
                        field.getName(),
                        this.manager.getConfigClass().getName()
                ));
            }
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %f is out of range (min: %f, max: %f).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            String key = category + field.getName();
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Double.MIN_VALUE ? null : minVal, maxVal == Double.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFloatField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.FloatValue.class.getSimpleName(), float.class, Float.class);
        PaCoConfigValues.FloatValue floatAnnotation = field.getAnnotation(PaCoConfigValues.FloatValue.class);
        field.setAccessible(true);
        try {
            float defaultValue = (float) this.getOrThrow(field);
            float minVal = floatAnnotation.minValue();
            float maxVal = floatAnnotation.maxValue();
            boolean showFullRange = floatAnnotation.showFullRange();
            if (minVal >= maxVal) {
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': The minimum must be less than the maximum.",
                        field.getName(),
                        this.manager.getConfigClass().getName()
                ));
            }
            if (defaultValue < minVal || defaultValue > maxVal)
                throw new IllegalArgumentException(String.format(
                        "Invalid value for field '%s' in class '%s': Default value %f is out of range (min: %f, max: %f).",
                        field.getName(),
                        this.manager.getConfigClass().getName(),
                        defaultValue,
                        minVal,
                        maxVal
                ));
            String key = category + field.getName();
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Float.MIN_VALUE ? null : minVal, maxVal == Float.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setConverter(value -> {
                            if (value instanceof Number number)
                                return number.floatValue();
                            throw new IllegalArgumentException("Config value is not a Number as expected for float.");
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLongField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.LongValue.class.getSimpleName(), long.class, Long.class);
        PaCoConfigValues.LongValue longAnnotation = field.getAnnotation(PaCoConfigValues.LongValue.class);
        field.setAccessible(true);
        try {
            long defaultValue = (long) this.getOrThrow(field);
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
            String key = category + field.getName();
            configSpec.define(key, defaultValue, o -> {
                if (o instanceof Integer || o instanceof Long) {
                    long longValue = ((Number) o).longValue();
                    return longValue >= minVal && longValue <= maxVal;
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setRange(minVal == Long.MIN_VALUE ? null : minVal, maxVal == Long.MAX_VALUE ? null : maxVal)
                        .setShowFullRange(showFullRange, minVal, maxVal)
                        .setConverter(value -> {
                            if (value instanceof Number number)
                                return number.longValue();
                            throw new IllegalArgumentException("Config value is not a Number as expected for long.");
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStringField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.StringValue.class.getSimpleName(), String.class);
        field.setAccessible(true);
        try {
            String defaultValue = (String) this.getOrThrow(field);
            String key = category + field.getName();
            configSpec.define(key, defaultValue);
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            this.dataHolders.put(key, holder.setPath(key));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleListField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.ListValue.class.getSimpleName(), List.class);
        PaCoConfigValues.ListValue listAnnotation = field.getAnnotation(PaCoConfigValues.ListValue.class);
        field.setAccessible(true);
        try {
            List<?> defaultValue = (List<?>) getOrThrow(field);
            String key = category + field.getName();
            configSpec.defineList(key, defaultValue, element -> listAnnotation.elementType().isInstance(element));
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            this.dataHolders.put(key, holder.setPath(key));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEnumField(Field field, String category) {
        if (!field.getType().isEnum())
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' must be of type Enum for EnumValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        field.setAccessible(true);
        try {
            Enum<?> defaultValue = (Enum<?>) this.getOrThrow(field);
            Class<? extends Enum> enumClass = (Class<? extends Enum>) field.getType();
            Object[] enumConstants = enumClass.getEnumConstants();
            List<String> enumNames = Arrays.stream(enumConstants)
                    .map(enumConstant -> ((Enum<?>) enumConstant).name())
                    .toList();
            String key = category + field.getName();
            configSpec.define(key, defaultValue.name(), value -> {
                if (value instanceof String stringVal) {
                    try {
                        Enum.valueOf(enumClass, stringVal);
                        return true;
                    } catch (IllegalArgumentException ignored) {}
                }
                return false;
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setValidValues(enumNames)
                        .setConverter(value -> {
                            if (value instanceof String stringVal)
                                return Enum.valueOf(enumClass, stringVal);
                            throw new IllegalArgumentException("Config value is not a String as expected for enum.");
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCustomField(Field field, String category) {
        if (!IPaCoConfigConverter.class.isAssignableFrom(field.getType()))
            throw new IllegalArgumentException(String.format(
                    "Field: '%s' in Class: '%s' object must implement IPaCoConfigConverter for CustomValue annotation.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        field.setAccessible(true);
        try {
            Object defaultValue = this.getOrThrow(field);
            IPaCoConfigConverter<?, ?> converter = (IPaCoConfigConverter<?, ?>) defaultValue;
            String key = category + field.getName();
            Object serializedDefault = converter.serialize();
            Class<?> expectedType = converter.getSerializedType();
            configSpec.define(key, serializedDefault, value -> {
                if (!expectedType.isInstance(value))
                    return false;
                Object casted = expectedType.cast(value);
                Predicate<Object> predicate = (Predicate<Object>) converter.getSerializedPredicate();
                return predicate.test(casted);
            });
            ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
            if (holder instanceof ConfigDataHolderEntry holderEntry)
                this.dataHolders.put(key, holderEntry
                        .setConverter(value -> {
                            if (expectedType.isInstance(value))
                                return this.deserializeHelper(converter, value);
                            throw new IllegalArgumentException(String.format("Custom config value '%s' is not expected type '%s'.", key, expectedType.getSimpleName()));
                        }).setPath(key)
                );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T, R> Object deserializeHelper(IPaCoConfigConverter<T, R> converter, Object value) {
        Class<R> type = converter.getSerializedType();
        R casted = type.cast(value);
        return converter.deserialize(casted);
    }

    private void handleComment(Field field, String category) {
        PaCoConfigValues.Comment commentAnnotation = field.getAnnotation(PaCoConfigValues.Comment.class);
        String key = category + field.getName();
        ConfigDataHolder holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry(field));
        this.dataHolders.put(key, holder.setComment(commentAnnotation.value(), commentAnnotation.padding()));
    }

    private void handleCategoryComment(Class<?> clazz, String category) {
        PaCoConfig.Comment commentAnnotation = clazz.getAnnotation(PaCoConfig.Comment.class);
        ConfigDataHolder holder = this.dataHolders.getOrDefault(category, new ConfigDataHolderCategory());
        holder.setPath(category);
        this.dataHolders.put(category, holder.setComment(commentAnnotation.value(), commentAnnotation.padding()));
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

    /**
     * Gets and returns the value of the given {@link Field} as an {@link Object}.
     * This method does not ensure the field is static or set to be accessible!
     *
     * @param field The {@link Field} used for value retrieval
     * @return The value assigned to the given {@link Field}
     * @throws IllegalAccessException if the {@code field} object is enforcing Java
     * language access control and the underlying field is inaccessible
     */
    private Object getOrThrow(Field field) throws IllegalAccessException {
        Object defaultObject = field.get(null);
        if (defaultObject == null)
            throw new IllegalArgumentException(String.format(
                    "Invalid value for field '%s' in class '%s': Null values are not allowed.",
                    field.getName(),
                    this.manager.getConfigClass().getName()
            ));
        return defaultObject;
    }
}
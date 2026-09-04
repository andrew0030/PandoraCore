package com.github.andrew0030.pandora_core.config.annotation;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.ConfigEntryFactory;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.PaCoConfigEntryManager;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BooleanEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.CategoryEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.StringEntry;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfig;
import com.github.andrew0030.pandora_core.config.annotation.annotations.PaCoConfigValues;
import com.github.andrew0030.pandora_core.config.annotation.converters.*;
import com.github.andrew0030.pandora_core.config.manager.*;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnotationHandler {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "AnnotationHandler");
    private static final Map<Class<? extends Annotation>, BiConsumer<Field, String>> ANNOTATION_HANDLERS = new HashMap<>();
    private static final Map<Class<?>, IPaCoConfigConverter<?, ?>> CONVERTER_CACHE = new HashMap<>();
    private final Map<String, ConfigDataHolder<?>> dataHolders = new LinkedHashMap<>();
    private final ConfigSpec configSpec = new ConfigSpec();
    private final PaCoConfigManager manager;
    private final String modId;
    private final String name;
    private final String configName;
    private final String subFolder;

    public AnnotationHandler(PaCoConfigManager manager) {
        this.manager = manager;
        PaCoConfig.Config configAnnotation = this.manager.getConfigClass().getAnnotation(PaCoConfig.Config.class);
        if (configAnnotation == null)
            throw new IllegalArgumentException("Class " + this.manager.getConfigClass().getName() + " must be annotated with @PaCoConfig.Config");
        this.modId = configAnnotation.modId();
        this.name = configAnnotation.name();
        this.configName = String.format("%s-%s", this.modId, configAnnotation.name());
        this.subFolder = this.initConfigSubFolder();
        // Initializes the: ANNOTATION_HANDLERS, configSpec
        this.initConfigCaches();
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

    /** @return The Mod ID that was used to register this config */
    public String getModId() {
        return this.modId;
    }

    /** @return The name that was used to register this config */
    public String getName() {
        return this.name;
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
        ANNOTATION_HANDLERS.put(PaCoConfigValues.BooleanValue.class, this::handleBooleanField);       // Boolean & boolean
        ANNOTATION_HANDLERS.put(PaCoConfigValues.IntegerValue.class, this::handleIntegerField);       // Integer & int
        ANNOTATION_HANDLERS.put(PaCoConfigValues.ByteValue.class, this::handleByteField);             // Byte & byte
        ANNOTATION_HANDLERS.put(PaCoConfigValues.ShortValue.class, this::handleShortField);           // Short & short
        ANNOTATION_HANDLERS.put(PaCoConfigValues.DoubleValue.class, this::handleDoubleField);         // Double & double
        ANNOTATION_HANDLERS.put(PaCoConfigValues.FloatValue.class, this::handleFloatField);           // Float & float
        ANNOTATION_HANDLERS.put(PaCoConfigValues.LongValue.class, this::handleLongField);             // Long & long
        ANNOTATION_HANDLERS.put(PaCoConfigValues.StringValue.class, this::handleStringField);         // String
        ANNOTATION_HANDLERS.put(PaCoConfigValues.ListValue.class, this::handleListField);             // List
        ANNOTATION_HANDLERS.put(PaCoConfigValues.EnumValue.class, this::handleEnumField);             // Enum
        ANNOTATION_HANDLERS.put(PaCoConfigValues.CustomValue.class, this::handleCustomField);         // Custom Classes
        ANNOTATION_HANDLERS.put(PaCoConfigValues.CustomListValue.class, this::handleCustomListField); // Custom Classes List
        ANNOTATION_HANDLERS.put(PaCoConfigValues.GuiEntryKey.class, this::handleGuiEntryKey);         // GUI Entry Key
        ANNOTATION_HANDLERS.put(PaCoConfigValues.GuiEntryTooltip.class, this::handleGuiEntryTooltip); // GUI Entry Tooltip
        ANNOTATION_HANDLERS.put(PaCoConfigValues.Comment.class, this::handleComment);                 // Comments

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
                BiConsumer<Field, String> consumer = ANNOTATION_HANDLERS.get(annotation.annotationType());
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
                this.handleCategory(clazz, classCategory);

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
    public ImmutableList<ConfigDataHolder<?>> getConfigDataHolders() {
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
            @SuppressWarnings("unchecked")
            ConfigDataHolder<Boolean> holder = (ConfigDataHolder<Boolean>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            ConfigEntryFactory factory = this.getConfigEntryFactory(field, BooleanEntry.class);
            holder.setPath(key);
            holder.setConfigEntryFactory(factory);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);
            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Integer> holder = (ConfigDataHolderEntry<Integer>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Integer.MIN_VALUE ? null : minVal, maxVal == Integer.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            ByteConfigConverter converter = new ByteConfigConverter(minVal, maxVal);
            configSpec.define(key, defaultValue, o -> {
                if (!(o instanceof Number)) return false;
                return converter.getSerializedPredicate().test((Number) o);
            });
            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Byte> holder = (ConfigDataHolderEntry<Byte>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Byte.MIN_VALUE ? null : minVal, maxVal == Byte.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            holder.setConverter(converter);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            ShortConfigConverter converter = new ShortConfigConverter(minVal, maxVal);
            configSpec.define(key, defaultValue, o -> {
                if (!(o instanceof Number)) return false;
                return converter.getSerializedPredicate().test((Number) o);
            });
            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Short> holder = (ConfigDataHolderEntry<Short>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Short.MIN_VALUE ? null : minVal, maxVal == Short.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            holder.setConverter(converter);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);
            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Double> holder = (ConfigDataHolderEntry<Double>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Double.MIN_VALUE ? null : minVal, maxVal == Double.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            FloatConfigConverter converter = new FloatConfigConverter(minVal, maxVal);


            // TODO test if using the converter here fixes the Forge IDE float bug
            configSpec.defineInRange(key, defaultValue, minVal, maxVal);


            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Float> holder = (ConfigDataHolderEntry<Float>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Float.MIN_VALUE ? null : minVal, maxVal == Float.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            holder.setConverter(converter);
            this.dataHolders.put(key, holder);
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
                        defaultValue, minVal, maxVal
                ));
            String key = category + field.getName();
            LongConfigConverter converter = new LongConfigConverter(minVal, maxVal);
            configSpec.define(key, defaultValue, o -> {
                if (!(o instanceof Number)) return false;
                return converter.getSerializedPredicate().test((Number) o);
            });
            @SuppressWarnings("unchecked")
            ConfigDataHolderEntry<Long> holder = (ConfigDataHolderEntry<Long>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setRange(minVal == Long.MIN_VALUE ? null : minVal, maxVal == Long.MAX_VALUE ? null : maxVal);
            holder.setShowFullRange(showFullRange, minVal, maxVal);
            holder.setConverter(converter);
            this.dataHolders.put(key, holder);
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
            @SuppressWarnings("unchecked")
            ConfigDataHolder<String> holder = (ConfigDataHolder<String>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            ConfigEntryFactory factory = this.getConfigEntryFactory(field, StringEntry.class);
            holder.setPath(key);
            holder.setConfigEntryFactory(factory);
            this.dataHolders.put(key, holder);
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
            ConfigDataHolder<?> holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            this.dataHolders.put(key, holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) field.getType();
            Object[] enumConstants = enumClass.getEnumConstants();
            List<String> enumNames = Arrays.stream(enumConstants)
                    .map(enumConstant -> ((Enum<?>) enumConstant).name())
                    .toList();
            String key = category + field.getName();
            EnumConfigConverter converter = new EnumConfigConverter(enumClass);
            configSpec.define(key, defaultValue.name(), o -> {
                if (!(o instanceof String)) return false;
                return converter.getSerializedPredicate().test(o);
            });
            ConfigDataHolderEntry<Enum<?>> holder = (ConfigDataHolderEntry<Enum<?>>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setValidValues(enumNames);
            holder.setConverter(converter);
            this.dataHolders.put(key, holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleCustomField(Field field, String category) {
        PaCoConfigValues.CustomValue customAnnotation = field.getAnnotation(PaCoConfigValues.CustomValue.class);
        field.setAccessible(true);
        try {
            Object defaultValue = this.getOrThrow(field);
            Class<? extends IPaCoConfigConverter<?, ?>> converterClass = customAnnotation.converter();
            IPaCoConfigConverter<?, ?> converter = this.getConverter(converterClass);
            if (!converter.getDeserializedType().isAssignableFrom(field.getType()))
                throw new IllegalArgumentException(String.format(
                        "Field '%s' type '%s' does not match converter expected type '%s'",
                        field.getName(),
                        field.getType().getSimpleName(),
                        converter.getDeserializedType().getSimpleName()
                ));
            String key = category + field.getName();
            Object serializedDefault = ((IPaCoConfigConverter<Object, Object>) converter).serialize(defaultValue);
            Class<?> expectedType = converter.getSerializedType();
            configSpec.define(key, serializedDefault, value -> {
                if (!expectedType.isInstance(value)) return false;
                Predicate<Object> predicate = (Predicate<Object>) converter.getSerializedPredicate();
                return predicate.test(value);
            });
            ConfigDataHolderEntry<Object> holder = (ConfigDataHolderEntry<Object>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            holder.setPath(key);
            holder.setConverter((IPaCoConfigConverter<Object, ?>) converter);
            this.dataHolders.put(key, holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handleCustomListField(Field field, String category) {
        this.checkFieldValidity(field, PaCoConfigValues.CustomListValue.class.getSimpleName(), List.class);
        PaCoConfigValues.CustomListValue customListAnnotation = field.getAnnotation(PaCoConfigValues.CustomListValue.class);
        field.setAccessible(true);
        try {
            List<?> defaultList = (List<?>) this.getOrThrow(field);
            Class<? extends IPaCoConfigConverter<?, ?>> converterClass = customListAnnotation.converter();
            IPaCoConfigConverter<?, ?> converter = this.getConverter(converterClass);
            String key = category + field.getName();
            List<Object> serializedDefaults = new ArrayList<>();
            Class<?> serializedType = converter.getSerializedType();
            Class<?> deserializedType = converter.getDeserializedType();
            for (Object element : defaultList) {
                if (!deserializedType.isInstance(element))
                    throw new IllegalArgumentException(String.format("Element in list '%s' is not of type '%s'", key, deserializedType.getSimpleName()));
                serializedDefaults.add(((IPaCoConfigConverter<Object, Object>) converter).serialize(element));
            }
            configSpec.defineList(key, serializedDefaults, element -> {
                if (!serializedType.isInstance(element)) return false;
                Predicate<Object> predicate = (Predicate<Object>) converter.getSerializedPredicate();
                return predicate.test(element);
            });
            ConfigDataHolderEntry<Object> holder = (ConfigDataHolderEntry<Object>) this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
            CustomListConfigConverter listConverter = new CustomListConfigConverter(converter);
            holder.setPath(key);
            holder.setConverter(listConverter);
            this.dataHolders.put(key, holder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads cached converters for the given {@code class}, and or creates a new instances and caches them if needed.
     *
     * @param key The {@link Class} that will be used as the {@code key} for the cache
     * @return An {@link IPaCoConfigConverter} instance that corresponds to the given {@code key}
     */
    @SuppressWarnings("unchecked")
    private <T extends IPaCoConfigConverter<?, ?>> T getConverter(Class<T> key) {
        return (T) CONVERTER_CACHE.computeIfAbsent(key, converter -> {
            try {
                // This should technically never fail as the annotation requires it, but better safe than sorry
                if (!IPaCoConfigConverter.class.isAssignableFrom(converter)) {
                    throw new IllegalArgumentException(String.format(
                            "Class: '%s' must implement IPaCoConfigConverter to be a valid converter.",
                            converter.getName()
                    ));
                }
                // Creates a new instance from the class constructor
                Constructor<?> constructor = converter.getDeclaredConstructor();
                if (!Modifier.isPublic(constructor.getModifiers()))
                    constructor.setAccessible(true);
                return (IPaCoConfigConverter<?, ?>) constructor.newInstance();
            } catch (NoSuchMethodException e) {
                // The converter class needs to have a no-arg constructor
                throw new IllegalArgumentException(String.format(
                        "Converter: '%s' must have a no-arg constructor", converter.getName()
                ));
            } catch (Exception e) {
                // Not sure why it would fail otherwise...
                throw new RuntimeException(String.format(
                        "Failed to instantiate converter: '%s'", converter.getName()
                ), e);
            }
        });
    }

    private void handleGuiEntryKey(Field field, String category) {
        PaCoConfigValues.GuiEntryKey keyAnnotation = field.getAnnotation(PaCoConfigValues.GuiEntryKey.class);
        String key = category + field.getName();
        ConfigDataHolder<?> holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
        Component component = Component.translatable(keyAnnotation.value());
        holder.setKeyComponent(component);
        this.dataHolders.put(key, holder);
    }

    private void handleGuiEntryTooltip(Field field, String category) {
        PaCoConfigValues.GuiEntryTooltip tooltipAnnotation = field.getAnnotation(PaCoConfigValues.GuiEntryTooltip.class);
        String key = category + field.getName();
        ConfigDataHolder<?> holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
        List<Component> components = new ArrayList<>();
        components.add(Component.translatable(tooltipAnnotation.value()));
        holder.setTooltipComponents(components);
        this.dataHolders.put(key, holder);
    }

    private void handleComment(Field field, String category) {
        PaCoConfigValues.Comment commentAnnotation = field.getAnnotation(PaCoConfigValues.Comment.class);
        String key = category + field.getName();
        ConfigDataHolder<?> holder = this.dataHolders.getOrDefault(key, new ConfigDataHolderEntry<>(field));
        holder.setComment(commentAnnotation.value(), commentAnnotation.padding());
        this.dataHolders.put(key, holder);
    }

    public void handleCategory(Class<?> clazz, String category) {
        ConfigDataHolder<?> holder = this.dataHolders.getOrDefault(category, new ConfigDataHolderCategory());
        holder.setPath(category);
        // Entry Factory
        ConfigEntryFactory factory = this.getCategoryConfigEntryFactory(clazz, CategoryEntry.class);
        holder.setConfigEntryFactory(factory);
        // Entry Key
        PaCoConfig.GuiEntryKey keyAnnotation = clazz.getAnnotation(PaCoConfig.GuiEntryKey.class);
        if (keyAnnotation != null) {
            Component component = Component.translatable(keyAnnotation.value());
            holder.setKeyComponent(component);
        }
        // Entry Tooltip
        PaCoConfig.GuiEntryTooltip tooltipAnnotation = clazz.getAnnotation(PaCoConfig.GuiEntryTooltip.class);
        if (keyAnnotation != null) {
            List<Component> components = new ArrayList<>();
            components.add(Component.translatable(tooltipAnnotation.value()));
            holder.setTooltipComponents(components);
        }
        // Comment
        PaCoConfig.Comment commentAnnotation = clazz.getAnnotation(PaCoConfig.Comment.class);
        if (commentAnnotation != null)
            holder.setComment(commentAnnotation.value(), commentAnnotation.padding());

        this.dataHolders.put(category, holder);
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

    private <T> ConfigEntryFactory getConfigEntryFactory(Field field, Class<? extends BaseConfigEntry<T>> defaultEntry) {
        PaCoConfigValues.GuiEntryType annotation = field.getAnnotation(PaCoConfigValues.GuiEntryType.class);
        if (annotation == null) return PaCoConfigEntryManager.getFactory(defaultEntry);
        return PaCoConfigEntryManager.getFactory(annotation.value());
    }

    private <T> ConfigEntryFactory getCategoryConfigEntryFactory(Class<?> clazz, Class<? extends BaseConfigEntry<T>> defaultEntry) {
        PaCoConfig.GuiEntryType annotation = clazz.getAnnotation(PaCoConfig.GuiEntryType.class);
        if (annotation == null) return PaCoConfigEntryManager.getFactory(defaultEntry);
        return PaCoConfigEntryManager.getFactory(annotation.value());
    }
}
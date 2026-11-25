package com.github.andrew0030.pandora_core.config.access;

import net.minecraftforge.fml.loading.FMLConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection helper for {@link FMLConfig} internals.
 * <p>
 * {@link FMLConfig} lives in {@code fmlloader} (BOOT module layer), so mixin accessors cant target it.
 */
public class FMLConfigAccess {
    // ConfigValue stuff
    private static final Field ENTRY_FIELD;
    private static final Field COMMENT_FIELD;
    private static final Field VALUE_TYPE_FIELD;
    // FMLConfig stuff
    // This is needed for a small workaround. Basically since PaCo shadows night-config,
    // referencing the Classes/Methods causes the game to crash in production, since the
    // paths get changed. But we want these paths to use the forge paths, so we use reflection.
    private static final Object CONFIG_DATA_INSTANCE;
    private static final Method GET_METHOD; // Config.get(String)

    static {
        try {
            // ConfigValue stuff
            ENTRY_FIELD = FMLConfig.ConfigValue.class.getDeclaredField("entry");
            COMMENT_FIELD = FMLConfig.ConfigValue.class.getDeclaredField("comment");
            VALUE_TYPE_FIELD = FMLConfig.ConfigValue.class.getDeclaredField("valueType");
            ENTRY_FIELD.setAccessible(true);
            COMMENT_FIELD.setAccessible(true);
            VALUE_TYPE_FIELD.setAccessible(true);
            // FMLConfig stuff
            Field instanceField = FMLConfig.class.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);
            Field configDataField = FMLConfig.class.getDeclaredField("configData");
            configDataField.setAccessible(true);
            CONFIG_DATA_INSTANCE = configDataField.get(instanceField.get(null));
            // This should technically never happen because FMLConfig gets loaded WAY before PaCo, but just in case...
            if (CONFIG_DATA_INSTANCE == null) throw new IllegalStateException("FMLConfig has not been loaded yet");
            GET_METHOD = CONFIG_DATA_INSTANCE.getClass().getMethod("get", String.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to access FMLConfig internals", e);
        }
    }

    private FMLConfigAccess() {}

    /** @return The {@code entry} field of the given {@link FMLConfig.ConfigValue} instance */
    public static String getEntry(FMLConfig.ConfigValue value) {
        return (String) FMLConfigAccess.read(ENTRY_FIELD, value);
    }

    /** @return The {@code comment} field of the given {@link FMLConfig.ConfigValue} instance */
    public static String getComment(FMLConfig.ConfigValue value) {
        return (String) FMLConfigAccess.read(COMMENT_FIELD, value);
    }

    /** @return The {@code valueType} field of the given {@link FMLConfig.ConfigValue} instance */
    public static Class<?> getValueType(FMLConfig.ConfigValue value) {
        return (Class<?>) FMLConfigAccess.read(VALUE_TYPE_FIELD, value);
    }

    /**
     * @param entry The config entry name that will be used for value retrieval
     * @return The <strong>raw</strong> config value, bypassing FML's resolving public getters
     */
    public static Object getRawValue(String entry) {
        try {
            return GET_METHOD.invoke(CONFIG_DATA_INSTANCE, entry);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Failed to read '%s' from fml.toml", entry), e);
        }
    }

    private static Object read(Field field, FMLConfig.ConfigValue value) {
        try {
            return field.get(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Failed to read '%s' from %s", field.getName(), value), e);
        }
    }
}
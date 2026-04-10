package com.github.andrew0030.pandora_core.config.factory_manager.modmenu;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.config.factory_manager.modmenu.class_loader.ModMenuCompatClassLoader;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

class ModMenuMissingStrategy implements ModMenuStrategy {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModMenuConfigScreenHelper", "ModMenuMissingStrategy");
    private static final ModMenuCompatClassLoader CLASS_LOADER;

    static {
        CLASS_LOADER = ModMenuCompatClassLoader.create(Set.copyOf(ModMenuConfigScreenHelper.FACTORY_PATHS.values()));
    }

    @Override
    public Optional<BiFunction<Screen, ModContainer, Screen>> getScreenFactory(String modId) {
        String path = ModMenuConfigScreenHelper.FACTORY_PATHS.get(modId);
        if (path == null) return Optional.empty();
        try {
            Class<?> clazz = Class.forName(path, true, CLASS_LOADER);
            Object configInstance = clazz.getDeclaredConstructor().newInstance();
            Method factoryMethod = clazz.getMethod("getModConfigScreenFactory");
            Object factory = factoryMethod.invoke(configInstance);
            if (factory == null) return Optional.empty();
            // Gets the "create" method (once on startup)
            Method createMethod = factory.getClass().getDeclaredMethod("create", Screen.class);
            createMethod.setAccessible(true);
            // Converts to a MethodHandle and binds it to the factory instance (once on startup).
            // Since we are using a custom class loader, we use "privateLookupIn", which explicitly
            // grants the lookup access to the factory's class, bypassing any loader isolation quirks.
            MethodHandle createHandle = MethodHandles.privateLookupIn(factory.getClass(), MethodHandles.lookup()).unreflect(createMethod).bindTo(factory);
            // Lambda captures the bound method handle
            BiFunction<Screen, ModContainer, Screen> opener = (current, ignored) -> {
                try {
                    return (Screen) createHandle.invoke(current);
                } catch (Throwable t) {
                    LOGGER.error("Config factory 'create' invocation failed in: '{}'", path);
                    return null;
                }
            };
            return Optional.of(opener);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Map<String, BiFunction<Screen, ModContainer, Screen>>> getScreenFactoryProvider(String modId) {
        String path = ModMenuConfigScreenHelper.FACTORY_PATHS.get(modId);
        if (path == null) return Optional.empty();
        try {
            Class<?> clazz = Class.forName(path, true, CLASS_LOADER);
            Object configInstance = clazz.getDeclaredConstructor().newInstance();
            Method providerMethod = clazz.getMethod("getProvidedConfigScreenFactories");
            // Map of ConfigScreenFactories that handle opening config screens for other mods "getModConfigScreenFactory"
            // has priority over this, so this won't replace config screens specified by that method
            @SuppressWarnings("unchecked")
            Map<?, ?> raw = (Map<?, ?>) providerMethod.invoke(configInstance);
            if (raw == null || raw.isEmpty()) return Optional.empty();
            // Small sanity check for the first entry
            Map.Entry<?, ?> first = raw.entrySet().iterator().next();
            if (!(first.getKey() instanceof String) || first.getValue() == null) {
                LOGGER.error("Provider map in '{}' has invalid structure (expected Map<String, ConfigScreenFactory<?>>)", path);
                return Optional.empty();
            }
            Map<String, BiFunction<Screen, ModContainer, Screen>> provider = new HashMap<>(raw.size());
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                String key = (String) entry.getKey();
                Object factory = entry.getValue();
                if (factory == null) continue;
                Method createMethod = factory.getClass().getDeclaredMethod("create", Screen.class);
                createMethod.setAccessible(true);
                // Converts to a MethodHandle and binds it to the factory instance (once on startup).
                // Since we are using a custom class loader, we use "privateLookupIn", which explicitly
                // grants the lookup access to the factory's class, bypassing any loader isolation quirks.
                MethodHandle createHandle = MethodHandles.privateLookupIn(factory.getClass(), MethodHandles.lookup()).unreflect(createMethod).bindTo(factory);
                // Lambda captures the bound method handle
                BiFunction<Screen, ModContainer, Screen> opener = (current, ignored) -> {
                    try {
                        return (Screen) createHandle.invoke(current);
                    } catch (Throwable t) {
                        LOGGER.error("Config factory 'create' invocation failed, provider for: '{}' in: '{}'", key, path);
                        return null;
                    }
                };
                provider.put(key, opener);
            }
            return provider.isEmpty() ? Optional.empty() : Optional.of(provider);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
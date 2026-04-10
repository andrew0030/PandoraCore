package com.github.andrew0030.pandora_core.config.factory_manager.simple;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class SimpleConfigScreenHelper {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "SimpleConfigScreenHelper");

    /** @return The {@code screenFactory} path, under {@code pandoracore} or {@link Optional#empty()} if none is found. */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<String> getPaCoScreenFactoryPath(ModMetadata metadata) {
        return Optional.ofNullable(metadata.getCustomValue("pandoracore"))
                .filter(val -> val.getType() == CustomValue.CvType.OBJECT)
                .map(CustomValue::getAsObject)
                .map(obj -> obj.get("configFactory"))
                .filter(val -> val.getType() == CustomValue.CvType.STRING)
                .map(CustomValue::getAsString);
    }

    /** @return The {@code screenFactory} path, under {@code catalogue} or {@link Optional#empty()} if none is found. */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<String> getCatalogueScreenFactoryPath(ModMetadata metadata) {
        return Optional.ofNullable(metadata.getCustomValue("catalogue"))
                .filter(val -> val.getType() == CustomValue.CvType.OBJECT)
                .map(CustomValue::getAsObject)
                .map(obj -> obj.get("configFactory"))
                .filter(val -> val.getType() == CustomValue.CvType.STRING)
                .map(CustomValue::getAsString);
    }

    /**
     * Returns a {@link BiFunction} that invokes the static {@code createConfigScreen} method, or
     * {@link Optional#empty()} if the {@code class}/{@code method} doesn't exist or has the wrong signature.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<BiFunction<Screen, ModContainer, Screen>> getScreenFactory(String path) {
        try {
            Class<?> clazz = Class.forName(path);
            // The expected signature: (Screen, ModContainer) -> Screen
            MethodType mt = MethodType.methodType(Screen.class, Screen.class, ModContainer.class);
            MethodHandle handle;
            try {
                handle = MethodHandles.publicLookup().findStatic(clazz, "createConfigScreen", mt);
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
                return Optional.empty(); // If creating the handle fails, we cant open a screen...
            }
            // BiFunction that invokes/delegates to "createConfigScreen", or returns a null screen on failure
            BiFunction<Screen, ModContainer, Screen> factory = (current, container) -> {
                try {
                    return (Screen) handle.invoke(current, container);
                } catch (Throwable t) {
                    LOGGER.error("Config factory 'createConfigScreen' invocation failed in: '{}'", path);
                    return null;
                }
            };
            return Optional.of(factory);
        } catch (ClassNotFoundException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Returns a {@link Map} of {@code modId} -> {@link BiFunction}, which invokes the static
     * {@code createConfigProvider} method, or {@link Optional#empty()} if the
     * {@code class}/{@code method} doesn't exist or has the wrong signature.
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    public static Optional<Map<String, BiFunction<Screen, ModContainer, Screen>>> getScreenFactoryProvider(String path) {
        try {
            Class<?> clazz = Class.forName(path);
            // The expected signature: () -> Map<String, BiFunction<Screen, ModContainer, Screen>>
            MethodType mt = MethodType.methodType(Map.class);
            MethodHandle handle;
            try {
                handle = MethodHandles.publicLookup().findStatic(clazz, "createConfigProvider", mt);
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
                return Optional.empty();
            }
            // Map of BiFunctions that handle opening config screens for other mods "createConfigScreen"
            // has priority over this, so this won't replace config screens specified by that method
            @SuppressWarnings("unchecked")
            Map<?, ?> raw = (Map<?, ?>) handle.invoke();
            if (raw == null || raw.isEmpty()) return Optional.empty();
            // Small sanity check for the first entry
            Map.Entry<?, ?> first = raw.entrySet().iterator().next();
            if (!(first.getKey() instanceof String) || !(first.getValue() instanceof BiFunction<?, ?, ?>)) {
                LOGGER.error("Provider map in '{}' has invalid structure (expected Map<String, BiFunction<Screen, ModContainer, Screen>>)", path);
                return Optional.empty();
            }
            return Optional.of((Map<String, BiFunction<Screen, ModContainer, Screen>>) raw);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }
}
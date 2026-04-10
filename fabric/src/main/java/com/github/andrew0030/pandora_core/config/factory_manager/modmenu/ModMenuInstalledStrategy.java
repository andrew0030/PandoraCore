package com.github.andrew0030.pandora_core.config.factory_manager.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

class ModMenuInstalledStrategy implements ModMenuStrategy {

    @Override
    public Optional<BiFunction<Screen, ModContainer, Screen>> getScreenFactory(String modId) {
        String path = ModMenuConfigScreenHelper.FACTORY_PATHS.get(modId);
        if (path == null) return Optional.empty();
        try {
            Class<?> clazz = Class.forName(path);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            ModMenuApi api = (ModMenuApi) instance;
            ConfigScreenFactory<?> factory = api.getModConfigScreenFactory();
            if (factory == null) return Optional.empty();
            // This invokes/delegates to "getModConfigScreenFactory", and caches "create" in the lambda
            BiFunction<Screen, ModContainer, Screen> opener = (current, ignored) -> (Screen) factory.create(current);
            return Optional.of(opener);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Map<String, BiFunction<Screen, ModContainer, Screen>>> getScreenFactoryProvider(String modId) {
        String path = ModMenuConfigScreenHelper.FACTORY_PATHS.get(modId);
        if (path == null) return Optional.empty();
        try {
            Class<?> clazz = Class.forName(path);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            ModMenuApi api = (ModMenuApi) instance;
            Map<String, ConfigScreenFactory<?>> factories = api.getProvidedConfigScreenFactories();
            if (factories == null || factories.isEmpty()) return Optional.empty();
            // This iterates over all factories retrieved by invoking/delegating to "getProvidedConfigScreenFactories",
            // and afterward caches "create" for each inside the lambdas
            Map<String, BiFunction<Screen, ModContainer, Screen>> openers = new HashMap<>();
            factories.forEach((key, factory) -> openers.put(key, (current, ignored) -> factory.create(current)));
            return Optional.of(openers);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }
}
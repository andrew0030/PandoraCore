package com.github.andrew0030.pandora_core.config.factory_manager.modmenu;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

interface ModMenuStrategy {
    Optional<BiFunction<Screen, ModContainer, Screen>> getScreenFactory(String modId);
    Optional<Map<String, BiFunction<Screen, ModContainer, Screen>>> getScreenFactoryProvider(String modId);
}
package com.github.andrew0030.pandora_core.config.factory_manager;

import com.github.andrew0030.pandora_core.config.factory_manager.modmenu.ModMenuConfigScreenHelper;
import com.github.andrew0030.pandora_core.config.factory_manager.simple.SimpleConfigScreenHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screens.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ConfigScreenFactoryManager {
    private static final Map<String, BiFunction<Screen, ModContainer, Screen>> FACTORIES_CACHE = new HashMap<>();

    static {
        ConfigScreenFactoryManager.initFactoriesCache();
    }

    private ConfigScreenFactoryManager() {}

    public static Optional<BiFunction<Screen, ModContainer, Screen>> getConfigScreenFactory(String modId) {
        return Optional.ofNullable(FACTORIES_CACHE.get(modId));
    }

    /** Loops over all mods, and caches found config screen providers */
    private static void initFactoriesCache() {
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            ModMetadata metadata = modContainer.getMetadata();
            String modId = metadata.getId();
            Optional<String> pandoracorePathOpt = SimpleConfigScreenHelper.getPaCoScreenFactoryPath(metadata);
            Optional<String> cataloguePathOpt = SimpleConfigScreenHelper.getCatalogueScreenFactoryPath(metadata);

            // Check Order: PaCo -> Catalogue -> ModMenu
            Optional<BiFunction<Screen, ModContainer, Screen>> chosenFactory =
                    pandoracorePathOpt.flatMap(SimpleConfigScreenHelper::getScreenFactory).or(() -> // PaCo Config Factory
                    cataloguePathOpt.flatMap(SimpleConfigScreenHelper::getScreenFactory)).or(() ->  // Catalogue Config Factory
                    ModMenuConfigScreenHelper.getScreenFactory(modId));                             // ModMenu Config Factory
            // If the mod has a valid config factory, we cache it
            // This has priority over factories provided by other mods
            chosenFactory.ifPresent(factory -> FACTORIES_CACHE.put(modId, factory));

            // This retrieves all factory providers, and (if found) inserts their factories into the cache. The
            // insertion only occurs if the mod isn't already present, as factories specified by mods have priority!
            // Check Order: PaCo -> Catalogue -> ModMenu
            if (pandoracorePathOpt.isPresent())
                SimpleConfigScreenHelper.getScreenFactoryProvider(pandoracorePathOpt.get()).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
            if (cataloguePathOpt.isPresent())
                SimpleConfigScreenHelper.getScreenFactoryProvider(cataloguePathOpt.get()).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
            ModMenuConfigScreenHelper.getScreenFactoryProvider(modId).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
        });
    }
}
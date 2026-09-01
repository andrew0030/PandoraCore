package com.github.andrew0030.pandora_core.config.factory_manager;

import com.github.andrew0030.pandora_core.config.manager.ForgeConfigManager;
import com.github.andrew0030.pandora_core.config.registry.PaCoConfigRegistry;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.*;
import java.util.function.BiFunction;

// TODO maybe/probably remove if not needed
public class ConfigScreenFactoryManager {
    private static final Map<String, BiFunction<Minecraft, Screen, Screen>> FACTORIES_CACHE = new HashMap<>();

    static {
        ConfigScreenFactoryManager.initFactoriesCache();
    }

    private ConfigScreenFactoryManager() {}

    public static Optional<BiFunction<Minecraft, Screen, Screen>> getConfigScreenFactory(String modId) {
        return Optional.ofNullable(FACTORIES_CACHE.get(modId));
    }

    /** Loops over all mods, and caches found config screen providers */
    private static void initFactoriesCache() {
        ModList.get().forEachModContainer((modId, modContainer) -> {
            IModInfo modInfo = modContainer.getModInfo();

            Optional<BiFunction<Minecraft, Screen, Screen>> customFactory = ConfigScreenHandler.getScreenFactoryFor(modInfo);

            if (customFactory.isPresent()) {
                FACTORIES_CACHE.put(modId, customFactory.get());
            } else {
                List<ModConfig> forgeConfigs = getForgeConfigsForMod(modId);
                if (!forgeConfigs.isEmpty()) {
                    for (ModConfig config : forgeConfigs) {
                        ForgeConfigManager manager = new ForgeConfigManager(config);
                        PaCoConfigRegistry.register(config, manager);
                    }

                    // TODO handle forge factories in a cleaner way for better compat with configured
                    FACTORIES_CACHE.put(modId, (mc, screen) -> {
                        if (screen instanceof IPaCoParentScreenGetter pacoParentScreenGetter) {
                            if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen) {
                                return PaCoConfigRegistry.openConfigScreen(modId, titleScreen, screen);
                            }
                        }
                        return PaCoConfigRegistry.openConfigScreen(modId, null, screen);
                    });
                }
            }
        });










        // TODO clean this up after implementing fallback logic

//        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
//            ModMetadata metadata = modContainer.getMetadata();
//            String modId = metadata.getId();
//            Optional<String> pandoracorePathOpt = SimpleConfigScreenHelper.getPaCoScreenFactoryPath(metadata);
//            Optional<String> cataloguePathOpt = SimpleConfigScreenHelper.getCatalogueScreenFactoryPath(metadata);
//
//            // Check Order: PaCo -> Catalogue -> ModMenu
//            Optional<BiFunction<Screen, ModContainer, Screen>> chosenFactory =
//                    pandoracorePathOpt.flatMap(SimpleConfigScreenHelper::getScreenFactory).or(() -> // PaCo Config Factory
//                            cataloguePathOpt.flatMap(SimpleConfigScreenHelper::getScreenFactory)).or(() ->  // Catalogue Config Factory
//                            ModMenuConfigScreenHelper.getScreenFactory(modId));                             // ModMenu Config Factory
//            // If the mod has a valid config factory, we cache it
//            // This has priority over factories provided by other mods
//            chosenFactory.ifPresent(factory -> FACTORIES_CACHE.put(modId, factory));
//
//            // This retrieves all factory providers, and (if found) inserts their factories into the cache. The
//            // insertion only occurs if the mod isn't already present, as factories specified by mods have priority!
//            // Check Order: PaCo -> Catalogue -> ModMenu
//            if (pandoracorePathOpt.isPresent())
//                SimpleConfigScreenHelper.getScreenFactoryProvider(pandoracorePathOpt.get()).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
//            if (cataloguePathOpt.isPresent())
//                SimpleConfigScreenHelper.getScreenFactoryProvider(cataloguePathOpt.get()).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
//            ModMenuConfigScreenHelper.getScreenFactoryProvider(modId).ifPresent(factories -> factories.forEach(FACTORIES_CACHE::putIfAbsent));
//        });
    }

    /** Gets forge's {@link ConfigTracker} to find all {@code CLIENT} and {@code COMMON} configs for a specific mod. */
    private static List<ModConfig> getForgeConfigsForMod(String modId) {
        List<ModConfig> configs = new ArrayList<>();
        for (ModConfig.Type type : List.of(ModConfig.Type.CLIENT, ModConfig.Type.COMMON)) {
            Set<ModConfig> configSet = ConfigTracker.INSTANCE.configSets().get(type);
            if (configSet != null) {
                for (ModConfig config : configSet) {
                    if (config.getModId().equals(modId)) {
                        configs.add(config);
                    }
                }
            }
        }
        return configs;
    }
}
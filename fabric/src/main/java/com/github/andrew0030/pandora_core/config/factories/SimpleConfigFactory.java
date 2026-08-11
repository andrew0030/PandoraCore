package com.github.andrew0030.pandora_core.config.factories;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.config.PaCoMainConfig;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public class SimpleConfigFactory {

    public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
        if (currentScreen instanceof IPaCoParentScreenGetter pacoParentScreenGetter) {
            if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen) {
//                return new PaCoScreen(titleScreen, screen);
                return new PaCoConfigScreen(PaCoConfigManager.getManager(PaCoMainConfig.class), titleScreen, currentScreen); // TODO clean up the manager logic for this
            }
        }
//        return new PaCoScreen(null, screen);
        return new PaCoConfigScreen(PaCoConfigManager.getManager(PaCoMainConfig.class), null, currentScreen);
    }

    //TODO: remove this when done with tests!
//    public static Map<String, BiFunction<Screen, ModContainer, Screen>> createConfigProvider() {
//        Map<String, BiFunction<Screen, ModContainer, Screen>> modConfigFactories = new HashMap<>();
//        FabricLoader.getInstance().getAllMods().forEach(container -> {
//            String id = container.getMetadata().getId();
//            if (id.equals(PandoraCore.MOD_ID)) return;
//            modConfigFactories.put(id, (currentScreen, ignored) -> new PaCoScreen(null, currentScreen));
//        });
//        return modConfigFactories;
//    }
}
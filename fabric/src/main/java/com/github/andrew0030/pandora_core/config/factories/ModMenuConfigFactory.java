package com.github.andrew0030.pandora_core.config.factories;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.TitleScreen;

@Environment(EnvType.CLIENT)
public class ModMenuConfigFactory implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            if (screen instanceof IPaCoParentScreenGetter pacoParentScreenGetter)
                if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen)
                    return new PaCoScreen(titleScreen, screen);
            return new PaCoScreen(null, screen);
        };
    }

    //TODO: remove this when done with tests!
//    @Override
//    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
//        Map<String, ConfigScreenFactory<?>> modConfigFactories = new HashMap<>();
//        FabricLoader.getInstance().getAllMods().forEach(container -> {
//            String id = container.getMetadata().getId();
//            if (id.equals(PandoraCore.MOD_ID)) return;
//            modConfigFactories.put(id, (currentScreen) -> new PaCoScreen(null, currentScreen));
//        });
//        return modConfigFactories;
//    }
}
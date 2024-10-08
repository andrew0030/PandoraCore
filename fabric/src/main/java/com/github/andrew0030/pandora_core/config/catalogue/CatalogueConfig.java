package com.github.andrew0030.pandora_core.config.catalogue;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public class CatalogueConfig {

    public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
        if (currentScreen instanceof IPaCoParentScreenGetter pacoParentScreenGetter)
            if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen)
                return new PaCoScreen(titleScreen, currentScreen);
        return new PaCoScreen(null, currentScreen);
    }
}
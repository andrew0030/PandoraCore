package com.github.andrew0030.pandora_core.config.modmenu;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoParentScreenGetter;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.TitleScreen;

@Environment(EnvType.CLIENT)
public class ModMenuConfig implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            if (screen instanceof IPaCoParentScreenGetter pacoParentScreenGetter)
                if (pacoParentScreenGetter.pandoraCore$getParentScreen() instanceof TitleScreen titleScreen)
                    return new PaCoScreen(titleScreen, screen);
            return new PaCoScreen(null, screen);
        };
    }
}
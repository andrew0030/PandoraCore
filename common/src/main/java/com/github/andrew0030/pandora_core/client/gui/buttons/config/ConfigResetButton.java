package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import net.minecraft.network.chat.Component;

public class ConfigResetButton extends ConfigActionButton {

    public ConfigResetButton(PaCoConfigScreen screen) {
        super(screen, Component.literal("Reset"), ConfigActionType.RESET); // TODO narration
        this.active = false;
    }

    @Override
    public void onPress() {
        // TODO reset logic...
    }
}
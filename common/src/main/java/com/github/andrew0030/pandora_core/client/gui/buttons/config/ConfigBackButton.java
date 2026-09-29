package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

// TODO narration
public class ConfigBackButton extends ConfigActionButton {

    public ConfigBackButton(PaCoConfigScreen screen) {
        super(screen, Component.literal("Exit"), ConfigActionType.BACK);
    }

    @Override
    public void onPress() {
        this.screen.onClose();
    }

    @NotNull
    @Override
    public Component getMessage() {
        if (this.screen.getCurrentNode() != this.screen.getRootNode() && this.screen.getCurrentNode().getParent() != null)
            return Component.literal("Back");
        return super.getMessage();
    }
}
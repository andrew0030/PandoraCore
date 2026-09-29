package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ConfigSaveButton extends ConfigActionButton {
    public ConfigSaveButton(PaCoConfigScreen screen) {
        super(screen, Component.literal("Save"), ConfigActionType.SAVE); // TODO narration
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.active = this.screen.getManager().hasPendingChanges();
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onPress() {
        this.screen.getManager().savePendingChanges();
    }
}
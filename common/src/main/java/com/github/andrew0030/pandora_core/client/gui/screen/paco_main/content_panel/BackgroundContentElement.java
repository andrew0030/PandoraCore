package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import net.minecraft.client.gui.GuiGraphics;

public class BackgroundContentElement extends BaseContentElement {
    private int height;

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, PaCoContentPanelManager manager) {
        int width = manager.screen.contentPanelWidth - 2;
        this.height = manager.screen.contentMenuHeight / 3;

        manager.screen.renderModBackground(manager.screen.selectedModButton.getModDataHolder(), graphics, manager.posX, manager.posY, width, this.height);
    }

    @Override
    public int getElementHeight() {
        return this.height;
    }
}
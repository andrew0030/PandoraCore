package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import net.minecraft.client.gui.GuiGraphics;

public class BackgroundContentElement extends BaseContentElement {

    public BackgroundContentElement(PaCoContentPanelManager manager) {
        this(manager, 0, 0);
    }

    public BackgroundContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        super(manager, offsetX, offsetY);
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PaCoScreen screen = this.manager.getScreen();
        screen.renderModBackground(screen.selectedModButton.getModDataHolder(), graphics, this.getX(), this.getY(), this.manager.getWidth(), this.getHeight());

//        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX + this.getOffsetX(), this.manager.getContentHeight() + this.getOffsetY(), this.manager.width, this.height, null, PaCoColor.color(255, 40, 40), 1);
    }

    @Override
    public int getHeight() {
        return this.manager.getScreen().contentMenuHeight / 3;
    }
}
package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.GuiGraphics;

public class BackgroundContentElement extends BaseContentElement {

    public BackgroundContentElement(PaCoContentPanelManager manager) {
        this(manager, 0, 0);
    }

    public BackgroundContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        super(manager, offsetX, offsetY);
        this.height = manager.getScreen().contentMenuHeight / 3;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PaCoScreen screen = this.manager.getScreen();
        screen.renderModBackground(screen.selectedModButton.getModDataHolder(), graphics, this.manager.posX, this.manager.getContentHeight(), this.manager.width, this.height);

        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX, this.manager.getContentHeight(), this.manager.width, this.height, null, PaCoColor.color(255, 40, 40), 1);
    }
}
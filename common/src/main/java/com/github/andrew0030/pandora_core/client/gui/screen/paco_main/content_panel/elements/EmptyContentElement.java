package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.GuiGraphics;

public class EmptyContentElement extends BaseContentElement {
    private final int height;

    public EmptyContentElement(PaCoContentPanelManager manager, int height) {
        super(manager, 0, 0);
        this.height = height;
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 40, 255), 1);
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
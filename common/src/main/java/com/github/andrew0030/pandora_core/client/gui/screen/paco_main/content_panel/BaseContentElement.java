package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import net.minecraft.client.gui.GuiGraphics;

public abstract class BaseContentElement {
    protected int height;
    protected final PaCoContentPanelManager manager;
    private final int offsetX;
    private final int offsetY;

    public BaseContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        this.manager = manager;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public int getElementHeight() {
        return this.height + offsetY;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }
}
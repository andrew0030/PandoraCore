package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import net.minecraft.client.gui.GuiGraphics;

public abstract class BaseContentElement {
    protected final PaCoContentPanelManager manager;
    private final int offsetX;
    private final int offsetY;
    private int relativeY;

    public BaseContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        this.manager = manager;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /** Needs to be called at the end of subclass constructor */
    public void initializeHeight() {
        this.relativeY = this.manager.getContentHeight() + this.getOffsetY();
        this.manager.addContentHeight(this.getElementHeight());
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public abstract int getHeight();

    public int getElementHeight() {
        return this.getHeight() + this.getOffsetY();
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public int getX() {
        return this.manager.getPosX() + this.getOffsetX();
    }

    public int getY() {
        return this.manager.getPosY() + this.relativeY;
    }
}
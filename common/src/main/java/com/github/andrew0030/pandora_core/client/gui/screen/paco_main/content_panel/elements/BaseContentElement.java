package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public abstract class BaseContentElement {
    protected final PaCoContentPanelManager manager;
    protected final Font font;
    private final int offsetX;
    private final int offsetY;
    private int relativeY;

    public BaseContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        this.manager = manager;
        this.font = Minecraft.getInstance().font;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /** @implNote Needs to be called at the end of subclass constructor! */
    public void initializeHeight() {
        this.relativeY = this.manager.getContentHeight() + this.getOffsetY();
        this.manager.addContentHeight(this.getElementHeight());
    }

    /**
     * Used to render this {@link BaseContentElement} instance.
     * @param graphics    The {@link GuiGraphics} context used for rendering
     * @param mouseX      The {@code x-axis} position of the mouse
     * @param mouseY      The {@code y-axis} position of the mouse
     * @param partialTick The current partialTick
     */
    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * @return   The {@code height} of this {@link BaseContentElement} instance.
     * @implNote This shouldn't include {@code offset}.
     */
    public abstract int getHeight();

    /** @return The {@code height} including {@code offset}, of this {@link BaseContentElement} instance. */
    public int getElementHeight() {
        return this.getHeight() + this.getOffsetY();
    }

    /** @return The {@code width} including {@code offset}, of this {@link BaseContentElement} instance. */
    public int getWidth() {
        return this.manager.getWidth() - this.getOffsetX();
    }

    /** @return The {@code x-axis} offset. */
    public int getOffsetX() {
        return this.offsetX;
    }

    /** @return The {@code y-axis} offset. */
    public int getOffsetY() {
        return this.offsetY;
    }

    /** @return The {@code x-axis} position (with offset). */
    public int getX() {
        return this.manager.getPosX() + this.getOffsetX();
    }

    /** @return The {@code y-axis} position (with offset). */
    public int getY() {
        return this.manager.getPosY() + this.relativeY;
    }
}
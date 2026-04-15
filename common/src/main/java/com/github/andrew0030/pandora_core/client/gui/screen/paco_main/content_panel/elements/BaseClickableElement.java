package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public abstract class BaseClickableElement extends BaseContentElement implements GuiEventListener, NarratableEntry {
    private final Component narrated;
    private final Runnable onClicked;
    protected boolean isHovered = false;
    protected boolean isFocused = false;
    private final PaCoScreen screen;

    public BaseClickableElement(PaCoContentPanelManager manager, String text, Runnable onClicked) {
        this(manager, 0, 0, text, onClicked);
    }

    public BaseClickableElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String text, Runnable onClicked) {
        super(manager, offsetX, offsetY);
        this.narrated = Component.literal(text);
        this.onClicked = onClicked;
        this.screen = manager.getScreen();
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.isMouseOver(mouseX, mouseY)) {
            this.onClicked.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        boolean hasScrollBar = manager.hasScrollBar();
        int posX = this.screen.modsPanelWidth + (hasScrollBar ? 12 : 4);
        int width = this.screen.contentPanelWidth - (hasScrollBar ? 10 : 2);
        if (PaCoGuiUtils.isMouseWithin(mouseX, mouseY, posX, this.screen.contentMenuHeightStart, width, this.screen.contentMenuHeight))
            return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() &&
                   mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isFocused && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE)) {
            this.onClicked.run();
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;
        if (focused) {
            // Moves element element into bounds
            this.moveElementIntoFocus(false);
        }
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    @NotNull
    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight()
        );
    }


    @Override
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
        return this.isFocused ? null : ComponentPath.leaf(this);
    }

    @NotNull
    @Override
    public NarrationPriority narrationPriority() {
        if (this.isFocused) return NarrationPriority.FOCUSED;
        if (this.isHovered) return NarrationPriority.HOVERED;
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, AbstractWidget.wrapDefaultNarrationMessage(this.narrated));
        if (this.isFocused) {
            output.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
        } else {
            output.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
        }
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered || this.isFocused;
    }

    /**
     * Moves the {@link BaseClickableElement} up/down and adds padding if needed,
     * to avoid the gradient, or being hidden post resizing the {@link PaCoScreen}.
     */
    public void moveElementIntoFocus(boolean moveToTop) {
        if (this.screen.contentScrollBar == null) return;
        int padding = 16; // We use padding because the gradient would interfere with the element otherwise.
        if (this.getY() < this.screen.contentMenuHeightStart + padding) { // Top Area
            int pixels = this.screen.contentMenuHeightStart - this.getY();
            this.screen.contentScrollBar.setValue(this.screen.contentScrollBar.getValue() - (pixels + padding));
        } else if (this.getY() + this.getHeight() > this.screen.contentMenuHeightStop - padding) { // Bottom Area
            int pixels = this.getY() + this.getHeight() - this.screen.contentMenuHeightStop;
            this.screen.contentScrollBar.setValue(this.screen.contentScrollBar.getValue() + (pixels + padding));
            // Used to move the element to the top of the list if possible
            if (moveToTop)
                this.screen.contentScrollBar.setValue(this.screen.contentScrollBar.getValue() + (this.screen.contentMenuHeight - this.getHeight() - padding * 2));
        }
    }
}
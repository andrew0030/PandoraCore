package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.CategoryEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigEntryNavigationButton extends AbstractButton {
    public static final int BUTTON_HEIGHT = 14;
    public static final int BUTTON_PADDING = 0; // TODO remove padding if I decide I don't want them to have padding
    public static final int DEPTH_INDENT = 6;
    private final PaCoConfigScreen screen;
    private final CategoryEntry entry;
    private final int depth;
    private final boolean isSelected;
    private final int activeLineIndex;
    private final boolean[] isBranchClosed;

    public ConfigEntryNavigationButton(PaCoConfigScreen screen, CategoryEntry entry, int y, int depth, boolean hasScrollBar) {
        super(
            screen.navMenuWidthStart + PaCoGuiUtils.PADDING_TWO, y,
            screen.navMenuWidth - (PaCoGuiUtils.PADDING_TWO * 2) - (hasScrollBar ? 8 : 0), BUTTON_HEIGHT,
            Component.literal("TODO")
        ); // TODO narration
        this.screen = screen;
        this.entry = entry;
        this.depth = depth;

        ConfigTreeNode currentNode = this.entry.getNode();
        ConfigTreeNode activeNode = screen.getCurrentNode();
        // Whether this button represents the currently selected node
        this.isSelected = currentNode == activeNode;

        // TODO probably replace the line render logic with blits instead of constructing lines
        // The active line index, we need this to render the tree lines highlighted at a certain depth
        ConfigTreeNode temp = currentNode;
        int lineIdx = -1;
        int stepsUp = 0;
        while (temp != null) {
            // Checks if the node is the currently selected one, and stops searching if it finds a match
            if (temp == activeNode) {
                lineIdx = this.depth - stepsUp;
                break;
            }
            // If no match was found we check the parent and repeat
            temp = temp.getParent();
            stepsUp++;
        }
        this.activeLineIndex = lineIdx;

        // Caches which parent branches are closed (the last child)
        boolean[] branchClosed = new boolean[depth];
        temp = currentNode;
        for (int i = depth - 1; i >= 0; i--) {
            ConfigTreeNode parent = temp.getParent();
            if (parent != null)
                branchClosed[i] = (parent.getLastChild() == temp);
            temp = parent;
        }
        this.isBranchClosed = branchClosed;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean mouseInBounds = PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.screen.navMenuWidthStart, this.screen.navMenuBottomStart, this.screen.navMenuWidth, this.screen.navMenuBottomHeight);
        this.isHovered = mouseInBounds && this.isHovered;
        // If buttons are fully outside the bounds of the panel, we "cull" them
        if (this.getY() + this.getHeight() > this.screen.navMenuBottomStart && this.getY() < this.screen.navMenuBottomStart + this.screen.navMenuBottomHeight) {
            int offset = this.getContentsOffset();

            // Background
            RenderSystem.enableBlend();
            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY() + 1, this.getWidth(), this.getHeight() - 2, 48, 122, 48, 48);
            if (this.isSelected) {
                graphics.blitNineSliced(PaCoScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3, 46, 46, 145, 123);
//            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 96, 122, 48, 48);
                int posY = this.getY();
                graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);
                posY += this.getHeight() - 1;
                graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);

                // TODO maybe add side bars ?
            }

            if (this.isHoveredOrFocused()) // TODO not really happy with the highlighting, maybe use padding and adjust hover logic instead?
                graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY() + 1, this.getWidth(), this.getHeight() - 2, 96, 122, 48, 48);

            // TODO colored components are tricky to turn gray, hence the shader color, this works but maybe yeeting all the brightness changes for text is better?
//        int color = this.entry.isVisible() || isActiveNode ? PaCoColor.WHITE : 11184810;
            if (!this.entry.isVisible() && !this.isSelected) RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1F);
            graphics.drawString(Minecraft.getInstance().font, this.entry.getEntryKey(), this.getX() + PaCoGuiUtils.PADDING_TWO + (DEPTH_INDENT * this.depth) + offset, this.getY() + PaCoGuiUtils.PADDING_THREE, PaCoColor.WHITE, false);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            // Renders the hierarchy lines in front of the text
            this.renderTreeLines(graphics, offset);
        }
    }

    private void renderTreeLines(GuiGraphics graphics, int offset) {
        // If depth isn't larger than 0 we don't need to render any lines
        if (this.depth <= 0) return;

        // TODO maybe yeet the line rendering fully if the button is selected?
        if (this.isSelected) RenderSystem.setShaderColor(1F, 1F, 1F, 0.2F);

        // Renders all the tree lines in front of the text, each loop representing a new depth
        for (int i = 0; i < this.depth; i++) {
            // Whether the branch at the current depth is already closed
            boolean isClosed = this.isBranchClosed[i];

            // Whether the line at the current depth should be highlighted
            boolean isActiveLine = (i == this.activeLineIndex);
            int lineColor = isActiveLine ? PaCoColor.color(255, 255, 255) : PaCoColor.color(100, 100, 100); // TODO maybe change line color? Although textures may replace them fully...

            // Vertical Line
            int depthOffset = (DEPTH_INDENT * i);
            int lineStartX = this.getX() + depthOffset + PaCoGuiUtils.PADDING_TWO + offset;
            int lineEndX = lineStartX + 1;
            int lineStartY = this.getY();
            boolean skipVerticalLine = (i < this.depth - 1) && isClosed;
            if (!skipVerticalLine) {
                int lineEndY = isClosed
                        ? this.getY() + (BUTTON_HEIGHT / 2)
                        : this.getY() + this.getHeight() + BUTTON_PADDING;
                graphics.fill(lineStartX, lineStartY, lineEndX, lineEndY,  lineColor);
            }

            // Horizontal Line
            if (i == this.depth - 1) {
                int lineWidth = 3;
                lineStartX += 1;
                lineEndX += lineWidth;
                lineStartY += (BUTTON_HEIGHT / 2);
                int lineEndY = lineStartY + 1;
                graphics.fill(lineStartX, lineStartY, lineEndX, lineEndY, lineColor);
            }
        }

        // TODO maybe yeet the line rendering fully if the button is selected?
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    @Override
    public int getY() {
        int y = super.getY();
        if (this.screen.navScrollBar != null)
            return Math.round((float) (y - this.screen.navScrollBar.getValue()));
        return y;
    }

    @Override
    public boolean isHoveredOrFocused() {
        boolean flag = super.isHoveredOrFocused();
        // TODO maybe disable this or adjust the movement to be gradual instead of a snap?
        if (flag) this.entry.moveEntryIntoFocus(false);
        return flag;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            // Moves Button into bounds if it's partially cut of.
            this.moveButtonIntoFocus(false);
        }
    }

    /** Moves the {@link ConfigEntryNavigationButton} up/down and adds padding if needed to avoid the gradient, or being hidden post resizing the {@link PaCoConfigScreen}. */
    public void moveButtonIntoFocus(boolean moveToTop) {
        if (this.screen.navScrollBar == null) return;
        int padding = 16; // We use padding because the gradient would interfere with the buttons otherwise.
        if (this.getY() < this.screen.navMenuBottomStart + padding) { // Top Area
            int pixels = this.screen.navMenuBottomStart - this.getY();
            this.screen.navScrollBar.setValue(this.screen.navScrollBar.getValue() - (pixels + padding));
        } else if (this.getY() + this.getHeight() > this.screen.navMenuHeightStop - padding) { // Bottom Area
            int pixels = this.getY() + this.getHeight() - this.screen.navMenuHeightStop;
            this.screen.navScrollBar.setValue(this.screen.navScrollBar.getValue() + (pixels + padding));
            // Used to move the button to the top of the list if possible
            if (moveToTop)
                this.screen.navScrollBar.setValue(this.screen.navScrollBar.getValue() + (this.screen.navMenuBottomHeight - this.getHeight() - padding * 2));
        }
    }

    @Override
    public void onPress() {
        PaCoConfigScreen subScreen = new PaCoConfigScreen(this.screen, this.entry.getNode());
        Minecraft.getInstance().setScreen(subScreen);
        // Makes it so if the navigation button was clicked directly it becomes focused
        subScreen.focusSelectedNavButton();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.screen.navMenuWidthStart, this.screen.navMenuBottomStart, this.screen.navMenuWidth, this.screen.navMenuBottomHeight))
            return super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    /** @return The total width of the rendered content */
    public int getContentsWidth() {
        return PaCoGuiUtils.PADDING_TWO + (DEPTH_INDENT * this.depth) + Minecraft.getInstance().font.width(this.entry.getEntryKey()) + PaCoGuiUtils.PADDING_TWO;
    }

    /** @return The amount of offset that should be applied to rendered content */
    public int getContentsOffset() {
        if (this.screen.navHrzScrollBar == null) return 0;
        return -Math.round((float) this.screen.navHrzScrollBar.getValue());
    }
}
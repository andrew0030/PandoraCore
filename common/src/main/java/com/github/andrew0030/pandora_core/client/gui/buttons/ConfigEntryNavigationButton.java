package com.github.andrew0030.pandora_core.client.gui.buttons;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.CategoryEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
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
    private final CategoryEntry entry;
    private final int depth;
    private final boolean isSelected;
    private final boolean isLastChild;
    private final int activeLineIndex;

    public ConfigEntryNavigationButton(PaCoConfigScreen screen, CategoryEntry entry, int y, int depth) {
        super(
            screen.navMenuWidthStart + PaCoConfigScreen.PADDING_TWO, y,
            screen.navMenuWidth - (PaCoConfigScreen.PADDING_TWO * 2), BUTTON_HEIGHT,
            Component.literal("TODO")
        ); // TODO narration
        this.entry = entry;
        this.depth = depth;

        ConfigTreeNode currentNode = this.entry.getNode();
        ConfigTreeNode parentNode = currentNode.getParent();
        ConfigTreeNode activeNode = screen.getCurrentNode();
        // Whether this button represents the currently selected node
        this.isSelected = currentNode == activeNode;
        // Whether this button's config tree node, is the last child of the parent's node
        boolean last = false;
        if (parentNode != null)
            last = parentNode.getLastChild() == currentNode;
        this.isLastChild = last;

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
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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
        graphics.drawString(Minecraft.getInstance().font, this.entry.getEntryKey(), this.getX() + PaCoConfigScreen.PADDING_TWO + (6 * this.depth), this.getY() + PaCoConfigScreen.PADDING_THREE, PaCoColor.WHITE, false);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        // Renders the hierarchy lines in front of the text
        this.renderTreeLines(graphics);
    }

    private void renderTreeLines(GuiGraphics graphics) {
        // If depth isn't larger than 0 we don't need to render any lines
        if (this.depth <= 0) return;

        // TODO maybe yeet the line rendering fully if the button is selected?
        if (this.isSelected) RenderSystem.setShaderColor(1F, 1F, 1F, 0.2F);

        // Renders all the tree lines in front of the text, each loop representing a new depth
        for (int i = 0; i < this.depth; i++) {
            // Whether the line at the current depth should be highlighted
            boolean isActiveLine = (i == this.activeLineIndex);
            int lineColor = isActiveLine ? PaCoColor.color(255, 255, 255) : PaCoColor.color(100, 100, 100); // TODO maybe change line color? Although textures may replace them fully...

            // Vertical Line
            int depthOffset = (6 * i);
            int lineStartX = this.getX() + depthOffset + PaCoConfigScreen.PADDING_TWO;
            int lineEndX = lineStartX + 1;
            int lineStartY = this.getY();
            int lineEndY = (i == this.depth - 1 && isLastChild)
                    ? this.getY() + (BUTTON_HEIGHT / 2)
                    : this.getY() + this.getHeight() + BUTTON_PADDING;
            graphics.fill(lineStartX, lineStartY, lineEndX, lineEndY,  lineColor);

            // Horizontal Line
            if (i == this.depth - 1) {
                int lineWidth = 3;
                lineStartX += 1;
                lineEndX += lineWidth;
                lineStartY += (BUTTON_HEIGHT / 2);
                lineEndY = lineStartY + 1;
                graphics.fill(lineStartX, lineStartY, lineEndX, lineEndY, lineColor);
            }
        }

        // TODO maybe yeet the line rendering fully if the button is selected?
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    @Override
    public boolean isHoveredOrFocused() {
        boolean flag = super.isHoveredOrFocused();
        // TODO maybe disable this or adjust the movement to be gradual instead of a snap?
        if (flag) this.entry.moveEntryIntoFocus(false);
        return flag;
    }

    @Override
    public void onPress() {
        // TODO handle click logic better
        this.entry.onPress();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
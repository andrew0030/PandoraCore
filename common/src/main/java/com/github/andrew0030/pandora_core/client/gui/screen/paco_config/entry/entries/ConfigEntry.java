package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.selection.PaCoConfigSelectionScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.IConfigManager;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

// TODO maybe/probably remove this later when I decide what kinda of config UI implementation I want
public class ConfigEntry  implements Renderable {
    protected final PaCoConfigSelectionScreen screen;
    private final int x, y, width, height;
    private final IConfigManager manager;
    private final Button widget;
    private boolean isHovered;
    private boolean isFocused;

    public ConfigEntry(PaCoConfigSelectionScreen screen, IConfigManager manager, int y, int height, boolean hasScrollBar) {
        this.screen = screen;
        this.x = this.screen.menuWidthStart + PaCoGuiUtils.PADDING_TWO + (hasScrollBar ? 8 : 0);
        this.y = y;
        this.width = this.screen.menuWidth - (PaCoGuiUtils.PADDING_TWO * 2) - (hasScrollBar ? 8 : 0);
        this.height = height;
        this.manager = manager;
        this.widget = new Button(this, Component.literal("TODO")); //TODO implement proper narration
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Hover Logic
//        this.isInBounds = this.screen.menuHeightStop >= this.getY() && this.screen.menuHeightStart < this.getY() + this.getHeight();
//        boolean mouseInBounds = this.screen.isMouseInEntriesBounds(mouseX, mouseY);
        this.isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() &&
                mouseY >= this.getY() - 1 && mouseY < this.getY() + this.getHeight() + 1; // NOTE: -+1 so there are no gaps between buttons when hovering them
//        this.isHovered = (this.navButton != null && this.navButton.isHoveredOrFocused()) || (mouseInBounds && isHovered);

        // No rendering is needed when the entry is out of bounds
//        if (!this.isInBounds) {
//            this.hoverAnimationProgress = 0F;
//            return;

        // Animation Times
//        long currentTime = Util.getMillis();
//        long deltaTime = Math.min(currentTime - this.lastUpdateTime, 100L); // Prevents huge animation jumps, if the game was paused
//        this.lastUpdateTime = currentTime;

        // Animation Progress
//        boolean isActive = this.isHoveredOrFocused();
//        if (isActive) {
//            if (this.hoverTime == 0) this.hoverTime = Util.getMillis();
//            this.hoverAnimationProgress += deltaTime * (1F / TEXT_ANIMATION_SPEED_MS);
//            if (this.hoverAnimationProgress > 1F) this.hoverAnimationProgress = 1F;
//        } else {
//            if (this.hoverTime != 0) hoverTime = 0;
//            this.hoverAnimationProgress -= deltaTime * (1F / TEXT_ANIMATION_SPEED_MS);
//            if (this.hoverAnimationProgress < 0F) this.hoverAnimationProgress = 0F;
//        }
//        float slideOffset = this.hoverAnimationProgress * TEXT_MOVEMENT_DISTANCE;

        // Element Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 48, 122, 48, 48);
//        RenderSystem.setShaderColor(1, 1, 1, isActive ? Easing.CUBIC_IN.apply(this.hoverAnimationProgress) : 1);
        // Background Highlight & Bars
//        if (isActive) {
//            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 96, 122, 48, 48);
//            int posY = this.getY() - 1;
//            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);
//            posY += this.getHeight() + 1;
//            graphics.blitRepeating(PaCoConfigScreen.TEXTURE, this.getX(), posY, this.getWidth(), 1, 144, 122, 48, 1);
//        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        // Config Key
        graphics.pose().pushPose();
        graphics.pose().translate(this.getX() + PaCoGuiUtils.PADDING_FOUR, this.getY() + PaCoGuiUtils.PADDING_FOUR, 1);
        graphics.drawString(Minecraft.getInstance().font, this.manager.getConfigName(), 0, 0, PaCoColor.WHITE, false);
        graphics.pose().popPose();




        // TODO: maybe move/change this to utilize minecraft's built in widget rendering ?
        // Widget
        this.widget.render(graphics, mouseX, mouseY, partialTick);

        // Debug Outline
//        PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 40, 40), 1);
    }

    public void onPress() {
//        this.moveEntryIntoFocus(false);
        this.widget.onPress();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y + this.getScrollOffset();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    protected int getScrollOffset() {
        if (this.screen.entriesScrollBar == null) return 0;
        return -Math.round((float) this.screen.entriesScrollBar.getValue());
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isFocused() {
        boolean wasFocused = this.isFocused;
        this.isFocused = this.widget.isFocused();
        // Moves the config entry into the menu panel bounds when focused
//        if (this.isFocused && !wasFocused)
//            this.moveEntryIntoFocus(false);

        return this.isFocused;
    }

    public boolean isHoveredOrFocused() {
        // NOTE: The isFocused check needs to be called first. The reason is that the check performs
        // state changes internally, and Java would skip it if isHovered returned true before the OR
        return this.isFocused() || this.isHovered();
    }

    public AbstractWidget getWidget() {
        return this.widget;
    }

    private static class Button extends AbstractWidget {
        private final ConfigEntry entry;
        private final PaCoConfigSelectionScreen screen;

        public Button(ConfigEntry entry, Component message) {
            super(entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), message);
            this.entry = entry;
            this.screen = entry.screen;
        }

        // NOTE: The code in this block should be implemented by all widgets used for config entries, the methods
        //       ensure that the widgets move along the config entries, and aren't clickable when out of bounds!
        // #########################################################################################################
        @Override
        public int getY() {
            return super.getY() + this.entry.getScrollOffset();
        }
        @Override
        public boolean isHovered() {
            return this.entry.isHovered() && super.isHovered();
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//            return this.entry.screen.isMouseInEntriesBounds(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button);
        }
        // #########################################################################################################

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // TODO replace this with a more proper category
            graphics.drawString(Minecraft.getInstance().font, ">",
                    this.getX() + this.width - 15,
                    this.getY() + (this.height / 2) - 4,
                    PaCoColor.WHITE, false);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.active && this.visible && CommonInputs.selected(keyCode)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onPress();
        }

        private void onPress() {
            // TODO look into a cleaner way to get the title screen
            PaCoConfigScreen subScreen = new PaCoConfigScreen(this.entry.manager, screen.titleScreen, screen);
            Minecraft.getInstance().setScreen(subScreen);
        }
    }
}
package com.github.andrew0030.pandora_core.client.gui.buttons.config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigActionButton extends AbstractWidget {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final int BUTTON_GAP = 8;
    public static final boolean HAS_EDGE_GAP = true;
    protected final Minecraft minecraft;
    protected final PaCoConfigScreen screen;
    protected final ConfigActionType type;

    public ConfigActionButton(PaCoConfigScreen screen, Component message, ConfigActionType type) {
        super(getButtonX(type, screen), screen.menuHeightStop + 6, getButtonWidth(type, screen), 18, message); // TODO fix the narration
        this.minecraft = Minecraft.getInstance();
        this.screen = screen;
        this.type = type;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = 0;
        int v = 198;
        if (this.isActive())
            u += this.isHoveredOrFocused() ? 72 : 36;

        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.blitNineSliced(TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1, 36, 18, u, v);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int textColor = this.isActive() ? PaCoColor.WHITE : 8421504;
        PaCoGuiUtils.drawCenteredString(graphics, this.minecraft.font, this.getMessage(), this.getX() + (this.getWidth() / 2), this.getY() + 5, textColor, true);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public abstract void onPress();

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

    /**
     * Calculates the exact width for a specific button in an evenly spaced row.
     * Distributes remainder pixels symmetrically, prioritizing the center button.
     */
    public static int getButtonWidth(ConfigActionType type, PaCoConfigScreen screen) {
        int index = type.ordinal();
        int totalButtons = ConfigActionType.values().length;
        int containerWidth = screen.menuWidth - (HAS_EDGE_GAP ? BUTTON_GAP * 2 : 0);
        int availableWidth = containerWidth - ((totalButtons - 1) * BUTTON_GAP);
        int baseWidth = availableWidth / totalButtons;
        int remainder = availableWidth % totalButtons;
        boolean extraPixel = false;
        if (remainder > 0) {
            boolean oddButtons = totalButtons % 2 != 0;
            boolean oddRemainder = remainder % 2 != 0;
            // Center button gets priority when both button count and remainder are odd
            if (oddButtons && oddRemainder && index == totalButtons / 2) {
                extraPixel = true;
            } else {
                int adjustedRem = (oddButtons && oddRemainder) ? remainder - 1 : remainder;
                int pairs = adjustedRem / 2;
                // Outer pairs get +1 pixel symmetrically
                if (index < pairs || index >= totalButtons - pairs) {
                    extraPixel = true;
                }
                // Leftover single pixel goes to the left-side inner button
                else if (adjustedRem % 2 != 0 && index == pairs) {
                    extraPixel = true;
                }
            }
        }
        return baseWidth + (extraPixel ? 1 : 0);
    }

    /** @return The exact x position for a button in an evenly spaced row */
    public static int getButtonX(ConfigActionType type, PaCoConfigScreen screen) {
        int x = screen.menuWidthStart + (HAS_EDGE_GAP ? BUTTON_GAP : 0);
        for (int i = 0; i < type.ordinal(); i++)
            x += ConfigActionButton.getButtonWidth(ConfigActionType.values()[i], screen) + BUTTON_GAP;
        return x;
    }

    public enum ConfigActionType {
        BACK, RESET, SAVE;
    }
}
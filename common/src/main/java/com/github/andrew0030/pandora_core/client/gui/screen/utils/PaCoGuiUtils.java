package com.github.andrew0030.pandora_core.client.gui.screen.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO maybe/probably move to client.utils
public class PaCoGuiUtils {

    private static final ArrayList<PaCoBorderSide> borderList = new ArrayList<>();

    public static void renderBox(GuiGraphics graphics, int posX, int posY, int width, int height, int boxColor) {
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, boxColor, null, null);
    }

    public static void renderBoxWithRim(GuiGraphics graphics, int posX, int posY, int width, int height, @Nullable Integer boxColor, @Nullable Integer rimColor, @Nullable Integer rimSize) {
        ArrayList<PaCoBorderSide> rims = null;
        if (rimColor != null && rimSize != null) {
            rims = PaCoGuiUtils.getBorderList();
            rims.add(PaCoBorderSide.TOP.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.RIGHT.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(rimSize));
            rims.add(PaCoBorderSide.LEFT.setColor(rimColor).setSize(rimSize));
        }
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, boxColor, rims);
    }

    public static void renderBoxWithRim(GuiGraphics graphics, int posX, int posY, int width, int height, @Nullable Integer boxColor, @Nullable List<PaCoBorderSide> rims) {
        // Box
        if (boxColor != null)
            graphics.fill(posX, posY, posX + width, posY + height, boxColor);
        // Rims
        if (rims != null) {
            for (PaCoBorderSide side : rims) {
                if (side.equals(PaCoBorderSide.TOP))
                    graphics.fill(posX, posY, posX + width, posY + side.getSize(), side.getColor());
                if (side.equals(PaCoBorderSide.RIGHT))
                    graphics.fill(posX + width - side.getSize(), posY, posX + width, posY + height, side.getColor());
                if (side.equals(PaCoBorderSide.BOTTOM))
                    graphics.fill(posX, posY + height - side.getSize(), posX + width, posY + height, side.getColor());
                if (side.equals(PaCoBorderSide.LEFT))
                    graphics.fill(posX, posY, posX + side.getSize(), posY + height, side.getColor());
            }
        }
    }

    /** @return Reusable list for {@link PaCoBorderSide} that clears itself when obtained. */
    public static ArrayList<PaCoBorderSide> getBorderList() {
        PaCoGuiUtils.borderList.clear();
        return PaCoGuiUtils.borderList;
    }

    /**
     * Alternative method to enable scissors, this method doesn't take start and end coordinates,
     * instead it only takes start coordinates and then the wanted width and height.<br/>
     * <strong>NOTE</strong>: it is important to call {@link GuiGraphics#disableScissor()} when done to disable the scissors.
     * @param graphics The {@link GuiGraphics}.
     * @param posX The starting X position.
     * @param posY The starting Y position.
     * @param width The width of the scissor rectangle.
     * @param height The height of the scissor rectangle.
     */
    public static void enableScissor(GuiGraphics graphics, int posX, int posY, int width, int height) {
        graphics.enableScissor(posX, posY, posX + width, posY + height);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, text, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the text to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow – whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, String text, int x, int y, int color, boolean dropShadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, text component, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the text {@link Component} to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow – whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        FormattedCharSequence charSequence = text.getVisualOrderText();
        graphics.drawString(font, charSequence, x - font.width(charSequence) / 2, y, color, dropShadow);
    }

    /**
     * Draws a centered string at the specified coordinates using the given font, formatted character sequence, color and dropShadow.
     * @param graphics The {@link GuiGraphics}.
     * @param font the {@link Font} to use for rendering.
     * @param text the {@link FormattedCharSequence} to draw.
     * @param x the x-coordinate of the center of the string.
     * @param y the y-coordinate of the string.
     * @param color the color of the string.
     * @param dropShadow – whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }
}
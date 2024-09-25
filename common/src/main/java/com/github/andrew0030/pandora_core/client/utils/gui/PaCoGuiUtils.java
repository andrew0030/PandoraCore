package com.github.andrew0030.pandora_core.client.utils.gui;

import com.github.andrew0030.pandora_core.client.utils.gui.enums.PaCoBorderSide;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO write javadoc for some of these methods that still need it.
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
     * @param dropShadow whether to apply a drop shadow to the string.
     */
    public static void drawCenteredString(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
    }

    /**
     * Checks if the mouse is within the bounds of the given rectangle.
     * @param mouseX The current x-coordinate of the mouse
     * @param mouseY The current y-coordinate of the mouse
     * @param x      The x-coordinate of the top left corner
     * @param y      The y-coordinate of the top left corner
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @return Whether the mouse is within the specified rectangle.
     */
    public static boolean isMouseWithin(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    /**
     * Draws a formatted text with word wrapping at the specified coordinates using the given font, text, line width,
     * color and drop shadow.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     */
    public static void drawWordWrap(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        for(FormattedCharSequence charSequence : font.split(text, lineWidth)) {
            graphics.drawString(font, charSequence, x, y, color, dropShadow);
            y += 9;
        }
    }

    /**
     * Draws a formatted text with word wrapping at the specified coordinates using the given font, text, line width,
     * color and drop shadow.<br/>
     * This version of the method also returns a new {@link Pair} containing the rendered text's width and height. If the
     * dimensions are not needed, instead call {@link PaCoGuiUtils#drawWordWrap}.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     * @return A {@link Pair} containing the width and height of the text.
     */
    public static Pair<Integer, Integer> drawWordWrapWithDimensions(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        int startY = y;
        int biggestWidth = 0;
        for(FormattedCharSequence charSequence : font.split(text, lineWidth)) {
            graphics.drawString(font, charSequence, x, y, color, dropShadow);
            biggestWidth = Math.max(font.width(charSequence), biggestWidth);
            y += 9;
        }
        return Pair.of(biggestWidth, y - startY);
    }

    /**
     * Draws a centered formatted text, with word wrapping of the specified coordinates
     * using the given font, text, line width, color and drop shadow.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     */
    public static void drawCenteredWordWrap(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        for(FormattedCharSequence charSequence : font.split(text, lineWidth / 2)) {
            PaCoGuiUtils.drawCenteredString(graphics, font, charSequence, x + lineWidth / 2, y, color, dropShadow);
            y += 9;
        }
    }

    /**
     * Draws a centered formatted text, with word wrapping of the specified coordinates
     * using the given font, text, line width, color and drop shadow.<br/>
     * This version of the method also returns a new {@link Pair} containing the rendered text's width and height. If the
     * dimensions are not needed, instead call {@link PaCoGuiUtils#drawCenteredWordWrap}.
     * @param graphics   The {@link GuiGraphics}.
     * @param font       The {@link Font} to use for rendering.
     * @param text       The {@link FormattedCharSequence} to draw.
     * @param x          The x-coordinate of the starting position.
     * @param y          The y-coordinate of the starting position.
     * @param lineWidth  The maximum width of each line before wrapping.
     * @param color      The color of the text.
     * @param dropShadow Whether to apply a drop shadow to the text.
     * @return A {@link Pair} containing the width and height of the text.
     */
    public static Pair<Integer, Integer> drawCenteredWordWrapWithDimensions(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int lineWidth, int color, boolean dropShadow) {
        int startY = y;
        int biggestWidth = 0;
        for(FormattedCharSequence charSequence : font.split(text, lineWidth / 2)) {
            PaCoGuiUtils.drawCenteredString(graphics, font, charSequence, x + lineWidth / 2, y, color, dropShadow);
            biggestWidth = Math.max(font.width(charSequence), biggestWidth);
            y += 9;
        }
        return Pair.of(biggestWidth, y - startY);
    }
}
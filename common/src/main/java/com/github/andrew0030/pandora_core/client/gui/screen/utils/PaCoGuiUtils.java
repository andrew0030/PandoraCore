package com.github.andrew0030.pandora_core.client.gui.screen.utils;

import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
}
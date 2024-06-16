package com.github.andrew0030.pandora_core.client.gui.screen.utils;

import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;
import java.util.List;

public class PaCoGuiUtils {

    public static void renderBoxWithRim(GuiGraphics graphics, int posX, int posY, int width, int height, int boxColor, @Nullable List<PaCoBorderSide> rims) {
        // Box
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
}
package com.github.andrew0030.pandora_core.client.gui.screen.elements;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoBorderSide;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ModsPanelScreenElement {

    public ModsPanelScreenElement() {

    }

    public void render(GuiGraphics graphics, int posX, int posY, int width, int height, int color, float slideProgress) {
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideProgress)), 0.0F, 0.0F);

        int rimColor = PaCoColor.color(255, 207, 207, 196);
//        int rimColor = PaCoColor.colorFromHSV(((PaCoClientTicker.getGame() * 8) + PaCoClientTicker.getPartialTick()) % 360, 1F, 1F);
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, color, List.of(
                PaCoBorderSide.TOP.setColor(rimColor).setSize(1),
                PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(1)
        ));

        int idx = 0;
        for (String modId : PandoraCore.getPaCoManagedMods()) {
            PaCoGuiUtils.renderBoxWithRim(graphics, posX + 2, posY + 2 + (31 * idx), width - 4, 30, PaCoColor.color(100, 0, 0, 0), null);

            // Mod Icon
            graphics.blit(new ResourceLocation(PandoraCore.MOD_ID, "pandora_core"), posX + 3, posY + 3 + (31 * idx), 0, 0, 0, 28, 28, 512, 512);

            // Mod ID
            graphics.drawString(Minecraft.getInstance().font, modId, posX + 32, posY + 3 + (31 * idx), PaCoColor.color(255, 255, 255), false);
            idx++;
        }

        graphics.pose().popPose();
    }
}
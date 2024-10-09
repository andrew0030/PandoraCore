package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

import java.awt.*;

public class TitleContentElement extends BaseContentElement {

    private final FormattedText text;
    private int height;

    public TitleContentElement(String text) {
        this.text = FormattedText.of(text);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, PaCoContentPanelManager manager) {
        graphics.pose().pushPose();
        graphics.pose().translate(manager.posX, manager.posY, 0F);
        graphics.pose().scale(2F, 2F, 1F);
        this.height = Mth.ceil(2F * PaCoGuiUtils.drawWordWrapWithDimensions(graphics, Minecraft.getInstance().font, this.text, 0, 0, 400, PaCoColor.WHITE, true).getSecond());
        graphics.pose().popPose();
    }

    @Override
    public int getElementHeight() {
        return this.height;
    }
}
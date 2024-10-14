package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;

public class TitleContentElement extends BaseContentElement {

    private final FormattedText text;

    public TitleContentElement(PaCoContentPanelManager manager, String text) {
        this(manager, 0, 0, text);
    }

    public TitleContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String text) {
        super(manager, offsetX, offsetY);
        this.text = FormattedText.of(text);
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.pose().pushPose();
        graphics.pose().translate(this.getX(), this.getY(), 0F);
        graphics.pose().scale(2F, 2F, 1F);
        PaCoGuiUtils.drawWordWrap(graphics, Minecraft.getInstance().font, this.text, 0, 0, (this.manager.getWidth() - this.getOffsetX()) / 2, PaCoColor.WHITE, true);
        graphics.pose().popPose();

//        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX, this.manager.getContentHeight() + getOffsetY(), this.manager.width, this.height, null, PaCoColor.color(40, 255, 40), 1);
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().font.split(this.text, (this.manager.getWidth() - this.getOffsetX()) / 2).size() * 9 * 2; // x2 because its scaled by 2;
    }
}
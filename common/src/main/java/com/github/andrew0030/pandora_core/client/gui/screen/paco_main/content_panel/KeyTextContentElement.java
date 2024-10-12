package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;

public class KeyTextContentElement extends BaseContentElement {

    private final String key;
    private final FormattedText value;
    private final int valueInset = 12;
    private int keyColor = PaCoColor.WHITE;
    private int valueColor = PaCoColor.WHITE;

    public KeyTextContentElement(PaCoContentPanelManager manager, String key, String value) {
        this(manager, 0, 0, key, value);
    }

    public KeyTextContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String key, String value) {
        super(manager, offsetX, offsetY);
        this.key = key;
        this.value = FormattedText.of(value);
        Font font = Minecraft.getInstance().font;
        this.height = 9;
        this.height += font.split(this.value, (this.manager.width - this.valueInset - this.getOffsetX())).size() * 9;
    }

    public KeyTextContentElement setKeyColor(int keyColor) {
        this.keyColor = keyColor;
        return this;
    }

    public KeyTextContentElement setValueColor(int valueColor) {
        this.valueColor = valueColor;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Key
        graphics.drawString(Minecraft.getInstance().font, this.key, this.manager.posX + this.getOffsetX(), this.manager.getContentHeight() + this.getOffsetY(), this.keyColor, true);
        // Value
        PaCoGuiUtils.drawWordWrap(
                graphics,
                Minecraft.getInstance().font,
                this.value,
                this.manager.posX + this.valueInset + this.getOffsetX(),
                9 + this.manager.getContentHeight() + this.getOffsetY(),
                this.manager.width - this.valueInset - this.getOffsetX(),
                this.valueColor,
                true
        );

//        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX, this.manager.getContentHeight() + getOffsetY(), this.manager.width, this.height, null, PaCoColor.color(40, 40, 255), 1);
    }
}
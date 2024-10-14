package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.StringUtil;

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
        this.initializeHeight();
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
        graphics.drawString(Minecraft.getInstance().font, this.key, this.getX(), this.getY(), this.keyColor, true);
        // Value
        PaCoGuiUtils.drawWordWrap(
                graphics,
                Minecraft.getInstance().font,
                this.value,
                this.getX() + this.valueInset,
                this.getY() + 11,
                this.manager.getWidth() - this.getOffsetX() - this.valueInset,
                this.valueColor,
                true
        );

//        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX, this.manager.getContentHeight() + getOffsetY(), this.manager.width, this.height, null, PaCoColor.color(40, 40, 255), 1);
    }

    @Override
    public int getHeight() {
        Font font = Minecraft.getInstance().font;
        return 11 + font.split(this.value, (this.manager.getWidth() - this.valueInset - this.getOffsetX())).size() * 9;
    }
}
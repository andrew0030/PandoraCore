package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
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
        graphics.drawString(this.font, this.key, this.getX(), this.getY(), this.keyColor, true);
        // Value
        PaCoGuiUtils.drawWordWrap(
                graphics,
                this.font,
                this.value,
                this.getX() + this.valueInset,
                this.getY() + 11,
                this.getWidth() - this.valueInset,
                this.valueColor,
                true
        );

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(40, 40, 255), 1);
    }

    @Override
    public int getHeight() {
        return 11 + this.font.split(this.value, (this.getWidth() - this.valueInset)).size() * 9;
    }
}
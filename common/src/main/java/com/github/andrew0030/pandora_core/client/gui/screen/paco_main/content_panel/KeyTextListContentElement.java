package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.StringUtil;

import java.util.HashMap;
import java.util.List;

public class KeyTextListContentElement extends BaseContentElement {

    private final String key;
    private final HashMap<Integer, Pair<FormattedText, Integer>> values = new HashMap<>();
    private int valueInset = 12;
    private int keyColor = PaCoColor.WHITE;
    private int valueColor = PaCoColor.WHITE;
    private String valuePrefix;

    public KeyTextListContentElement(PaCoContentPanelManager manager, String key, List<String> value) {
        this(manager, 0, 0, key, value);
    }

    public KeyTextListContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String key, List<String> values) {
        super(manager, offsetX, offsetY);
        this.key = key;
        Font font = Minecraft.getInstance().font;
        // Calculates and stores the values with corresponding heights
        for (int i = 0; i < values.size(); i++) {
            FormattedText value = FormattedText.of(values.get(i));
            int valueHeight = font.split(value, this.manager.width - this.valueInset - this.getOffsetX()).size() * 9;
            this.values.put(i, Pair.of(value, valueHeight));
        }
        // Calculates the total height
        this.height = 11 + this.values.values().stream().mapToInt(Pair::getSecond).sum();
    }

    public KeyTextListContentElement setKeyColor(int keyColor) {
        this.keyColor = keyColor;
        return this;
    }

    public KeyTextListContentElement setValueColor(int valueColor) {
        this.valueColor = valueColor;
        return this;
    }

    public KeyTextListContentElement setValuePrefix(String valuePrefix) {
        this.valuePrefix = valuePrefix;
        Font font = Minecraft.getInstance().font;
        this.valueInset += font.width(valuePrefix);
        // Updates the heights of the stored values
        for (int i = 0; i < this.values.size(); i++) {
            FormattedText value = this.values.get(i).getFirst();
            int valueHeight = font.split(value, this.manager.width - this.valueInset - this.getOffsetX()).size() * 9;
            this.values.put(i, Pair.of(value, valueHeight));
        }
        // Updates the total height
        this.height = 11 + this.values.values().stream().mapToInt(Pair::getSecond).sum();
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Key
        graphics.drawString(Minecraft.getInstance().font, this.key, this.manager.posX + this.getOffsetX(), this.manager.getContentHeight() + this.getOffsetY(), this.keyColor, true);
        // Value
        int lineOffsetY = 11;
        Font font = Minecraft.getInstance().font;
        for (int i = 0; i < this.values.size(); i++) {
            int posX = this.manager.posX + this.valueInset + this.getOffsetX();
            int posY = lineOffsetY + this.manager.getContentHeight() + this.getOffsetY();
            // Prefix (if needed)
            if (!StringUtil.isNullOrEmpty(this.valuePrefix))
                graphics.drawString(font, this.valuePrefix, posX - font.width(this.valuePrefix), posY, this.valueColor);
            // Value
            FormattedText lineValue = this.values.get(i).getFirst();
            PaCoGuiUtils.drawWordWrap(graphics, font, lineValue, posX, posY, this.manager.width - this.valueInset - this.getOffsetX(), this.valueColor, true);
            lineOffsetY += this.values.get(i).getSecond();
        }

//        PaCoGuiUtils.renderBoxWithRim(graphics, this.manager.posX, this.manager.getContentHeight() + getOffsetY(), this.manager.width, this.height, null, PaCoColor.color(255, 255, 40), 1);
    }
}

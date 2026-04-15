package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;

public class ClickableTextElement extends BaseClickableElement {
    private final Component component;
    private final Component componentUnderlined;
    private final int textWidth;
    private final int textHeight;

    public ClickableTextElement(PaCoContentPanelManager manager, String text, Runnable onClicked) {
        this(manager, 0, 0, text, onClicked);
    }

    public ClickableTextElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String text, Runnable onClicked) {
        super(manager, offsetX, offsetY, text, onClicked);
        this.component = Component.literal(text).withStyle(style -> style.withColor(ChatFormatting.BLUE));
        this.componentUnderlined = ComponentUtils.mergeStyles(this.component.copy(), Style.EMPTY.withUnderlined(true));
        this.textWidth = this.font.width(component);
        this.textHeight = this.font.lineHeight;
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // Component
        Component component = this.isHoveredOrFocused() ? this.componentUnderlined : this.component;
        graphics.drawString(this.font, component, this.getX(), this.getY(), PaCoColor.WHITE, true);

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 128, 0), 1);
    }

    @Override
    public int getHeight() {
        return this.textHeight;
    }

    @Override
    public int getWidth() {
        return this.textWidth;
    }
}
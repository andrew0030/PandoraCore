package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class ConfigButtonElement extends BaseClickableElement {
    private final int size = 18;

    public ConfigButtonElement(PaCoContentPanelManager manager, String text, Runnable onClicked) {
        this(manager, 0, 0, text, onClicked);
    }

    public ConfigButtonElement(PaCoContentPanelManager manager, int offsetX, int offsetY, String text, Runnable onClicked) {
        super(manager, offsetX, offsetY, text, onClicked);
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int u = 0;
        if (this.isHoveredOrFocused())
            u = this.size;

        float time = (PaCoClientTicker.getGlobal() + PaCoClientTicker.getPartialTick()) * 0.16F;
        float pulse = 0.5F + 0.5F * Mth.sin(time);
        float scaleOffset = 1.0F + (2.0F / 18.0F) * pulse;
        graphics.pose().pushPose();
        graphics.pose().translate(this.getX(), this.getY(), 0);
        // TODO: Have a discussion with team about config button pulsing
//        graphics.pose().translate(9, 9, 0);
//        graphics.pose().scale(scaleOffset, scaleOffset, 1);
//        graphics.pose().translate(-9, -9, 0);
        graphics.blit(PaCoScreen.TEXTURE, 0, 0, u, 180, this.size, this.size);
        graphics.pose().popPose();

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(128, 0, 255), 1);
    }

    @Override
    public int getHeight() {
        return this.size;
    }

    @Override
    public int getWidth() {
        return this.size;
    }
}
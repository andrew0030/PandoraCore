package com.github.andrew0030.pandora_core.client.gui.buttons;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ModsFilterButton extends AbstractButton {

    public ModsFilterButton(int x, int y, Component message) {
        super(x, y, 18, 18, message);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = 0;
        int v = 0;
        if (this.isHoveredOrFocused()) {
            v += 18;
            graphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Test Tooltip"), this.getX() + 10, this.getY() + 17);
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        graphics.blit(PaCoScreen.TEXTURE, this.getX(), this.getY(), u, v, this.getWidth(), this.getHeight());
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
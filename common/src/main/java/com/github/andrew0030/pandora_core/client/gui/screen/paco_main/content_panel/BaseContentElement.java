package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import net.minecraft.client.gui.GuiGraphics;

public abstract class BaseContentElement {

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, PaCoContentPanelManager manager);

    public abstract int getElementHeight();
}
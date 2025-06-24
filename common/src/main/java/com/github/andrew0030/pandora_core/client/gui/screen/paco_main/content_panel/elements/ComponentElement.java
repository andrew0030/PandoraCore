package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ComponentElement extends BaseContentElement {
    private final Component component;

    public ComponentElement(PaCoContentPanelManager manager, Component component) {
        this(manager, 0, 0, component);
    }

    public ComponentElement(PaCoContentPanelManager manager, int offsetX, int offsetY, Component component) {
        super(manager, offsetX, offsetY);
        this.component = component;
        this.initializeHeight();
    }

    public Component getComponent() {
        return this.component;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Component
        graphics.drawString(Minecraft.getInstance().font, this.component, this.getX(), this.getY(), PaCoColor.WHITE, true);
    }

    @Override
    public int getHeight() {
        return 10;
    }
}
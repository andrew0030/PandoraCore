package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class PaCoContentPanelManager {

    private final List<BaseContentElement> ELEMENTS = new ArrayList<>();
    private int contentHeight = 0;

    public void renderElements(GuiGraphics graphics) {
        for (BaseContentElement element : ELEMENTS) {
            element.render(graphics);
        }
    }

    public int getContentHeight() {
        return this.contentHeight;
    }
}
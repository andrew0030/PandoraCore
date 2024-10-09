package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class PaCoContentPanelManager {
    private final List<BaseContentElement> elements = new ArrayList<>();
    public final PaCoScreen screen;
    public final int posX;
    public final int posY;
    public final int width;
    public final int height;
    private int contentHeight = 0;

    public PaCoContentPanelManager(PaCoScreen screen) {
        this.screen = screen;
        this.posX = screen.modsPanelWidth + PaCoScreen.PADDING_FOUR;
        this.posY = screen.contentMenuHeightStart;
        this.width = screen.contentPanelWidth - PaCoScreen.PADDING_TWO;
        this.height = screen.contentMenuHeight;
    }

    public void addElement(BaseContentElement element) {
        this.elements.add(element);
    }

    public void renderElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (BaseContentElement element : this.elements) {
            element.render(graphics, mouseX, mouseY, partialTick, this);
        }
    }

    public int getContentHeight() {
        return this.contentHeight;
    }
}
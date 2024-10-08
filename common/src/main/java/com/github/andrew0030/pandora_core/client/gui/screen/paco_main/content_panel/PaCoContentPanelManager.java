package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaCoContentPanelManager {
    private final List<BaseContentElement> elements = new ArrayList<>();
    private final PaCoScreen screen;
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

    public void buildContentPanel(ModDataHolder holder) {
        this.clearElements();
        this.elements.add(new BackgroundContentElement(this));
        this.elements.add(new TitleContentElement(this, PaCoScreen.PADDING_FOUR, -16, holder.getModName()));
    }

    public void clearElements() {
        this.elements.clear();
    }

    public void renderElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.contentHeight = this.posY; // We start at Y because the content panel doesn't start at the top of the screen
        for (BaseContentElement element : this.elements) {
            element.render(graphics, mouseX, mouseY, partialTick);
            this.contentHeight += Math.max(0, element.getElementHeight());
        }
    }

    public int getContentHeight() {
        return this.contentHeight;
    }

    public PaCoScreen getScreen() {
        return this.screen;
    }
}
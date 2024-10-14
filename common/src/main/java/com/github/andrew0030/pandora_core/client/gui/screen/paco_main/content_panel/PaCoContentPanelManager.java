package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class PaCoContentPanelManager {
    private final List<BaseContentElement> elements = new ArrayList<>();
    private final PaCoScreen screen;
    private int posX;
    private final int posY;
    private int width;
    private final int height;
    private int contentHeight = 0;
    private boolean hasScrollBar;

    public PaCoContentPanelManager(PaCoScreen screen) {
        this.screen = screen;
        this.posX = screen.modsPanelWidth + PaCoScreen.PADDING_FOUR;
        this.posY = screen.contentMenuHeightStart;
        this.width = screen.contentPanelWidth - PaCoScreen.PADDING_TWO;
        this.height = screen.contentMenuHeight;

        if (this.getScreen().selectedModButton != null)
            this.buildContentPanel(this.getScreen().selectedModButton.getModDataHolder());
    }

    public void buildContentPanel(ModDataHolder holder) {
        this.clearElements();
        this.elements.add(new BackgroundContentElement(this));
        this.elements.add(new TitleContentElement(this, PaCoScreen.PADDING_FOUR, -16, holder.getModName()));
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, PaCoScreen.MOD_VERSION_KEY.getString(), holder.getModVersion()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, "Warning(s):", List.of("This is a warning and you are in trouble!", "This is another warning, hehe", "This is a banana...", "Anyone want a cup of tea?", "I think that's enough entries.", "Why are you reading all of this?"), "• ").setValueColor(PaCoScreen.SOFT_RED_TEXT_COLOR));
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, PaCoScreen.MOD_DESCRIPTION_KEY.getString(), holder.getModDescription()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, "License:", holder.getModLicense()).setValueColor(PaCoColor.color(160, 160, 160)));
//        if (!holder.getModAuthors().isEmpty()) // We only add the authors if there are any specified
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, "Author(s):", holder.getModAuthors()).setValueColor(PaCoColor.color(160, 160, 160)));
        boolean isForge = Services.PLATFORM.getPlatformName().equals("Forge");
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, isForge ? "Credits:" : "Contributor(s):", holder.getModCredits()).setValueColor(PaCoColor.color(160, 160, 160)));

        // If the height of the elements is greater than the panel size we flag as "needs scroll bar" and recalculate the elements with the new dimensions
        if (!this.hasScrollBar() && this.getContentHeight() > this.getScreen().contentMenuHeight) {
            this.hasScrollBar = true;
            this.posX += 8;
            this.width -= 8;
            this.buildContentPanel(holder);
        }
    }

    public void clearElements() {
        this.elements.clear();
        this.contentHeight = 0;
    }

    public void resetBounds() {
        this.hasScrollBar = false;
        this.posX = screen.modsPanelWidth + PaCoScreen.PADDING_FOUR;
        this.width = screen.contentPanelWidth - PaCoScreen.PADDING_TWO;
    }

    public void renderElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
//        this.contentHeight = this.posY; // We start at Y because the content panel doesn't start at the top of the screen
        for (BaseContentElement element : this.elements) {
            element.render(graphics, mouseX, mouseY, partialTick);
//            this.contentHeight += Math.max(0, element.getElementHeight());
        }
    }

    public int getContentHeight() {
        return this.contentHeight;
    }

    public void addContentHeight(int amount) {
        this.contentHeight += Math.max(0, amount);
    }

    public boolean hasScrollBar() {
        return this.hasScrollBar;
    }

    public PaCoScreen getScreen() {
        return this.screen;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
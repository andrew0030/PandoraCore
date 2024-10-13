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
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, PaCoScreen.MOD_VERSION_KEY.getString(), holder.getModVersion()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, "Warning(s):", List.of("This is a warning and you are in trouble!", "This is another warning, hehe", "This is a banana...", "Anyone want a cup of tea?", "I think that's enough entries.", "Why are you reading all of this?")).setValueColor(PaCoScreen.SOFT_RED_TEXT_COLOR).setValuePrefix("• "));
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, PaCoScreen.MOD_DESCRIPTION_KEY.getString(), holder.getModDescription()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new KeyTextContentElement(this, PaCoScreen.PADDING_FOUR, 4, "License:", holder.getModLicense()).setValueColor(PaCoColor.color(160, 160, 160)));
//        if (!holder.getModAuthors().isEmpty()) // We only add the authors if there are any specified
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, "Author(s):", holder.getModAuthors()).setValueColor(PaCoColor.color(160, 160, 160)));
        boolean isForge = Services.PLATFORM.getPlatformName().equals("Forge");
        this.elements.add(new KeyTextListContentElement(this, PaCoScreen.PADDING_FOUR, 4, isForge ? "Credits:" : "Contributor(s):", holder.getModCredits()).setValueColor(PaCoColor.color(160, 160, 160)));
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
package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements.*;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PaCoContentPanelManager {
    // Content Panel | With Active Mod
    public static final Component MOD_VERSION_KEY = Component.translatable("gui.pandora_core.paco.content.mod.version.key");
    public static final Component MOD_WARNING_KEY = Component.translatable("gui.pandora_core.paco.content.mod.warning.key");
    public static final Component MOD_DESCRIPTION_KEY = Component.translatable("gui.pandora_core.paco.content.mod.description.key");
    public static final Component MOD_AUTHORS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.authors.key");
    public static final Component MOD_CREDITS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.credits.key");
    public static final Component MOD_CONTRIBUTORS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.contributors.key");
    public static final Component MOD_LICENSE_KEY = Component.translatable("gui.pandora_core.paco.content.mod.license.key");
    // Content Panel | No Active Mod
    // TODO
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
        int paddingX = PaCoScreen.PADDING_FOUR;
        int paddingY = 8;
        this.elements.add(new BackgroundContentElement(this));
        this.elements.add(new TitleContentElement(this, paddingX, -16, holder.getModName()));
        this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, MOD_VERSION_KEY.getString(), holder.getModVersion()).setValueColor(PaCoColor.color(160, 160, 160)));
        if (holder.hasModWarnings()) // We only add warnings if there are any
            this.elements.add(new KeyTextListContentElement(this, paddingX, paddingY, MOD_WARNING_KEY.getString(), holder.getModWarnings().stream().map(Component::getString).toList(), "• ").setValueColor(PaCoScreen.SOFT_RED_TEXT_COLOR));


        // TODO: tweak this text
        if (holder.isOutdated()) {
            String value = "New version available:\n\n" + holder.getUpdateInfo().get().getChangelog();
            this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, "Update Available:", value).setValueColor(PaCoColor.color(160, 160, 160)));
        }



        this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, MOD_DESCRIPTION_KEY.getString(), holder.getModDescription()).setValueColor(PaCoColor.color(160, 160, 160)));
        if (holder.hasModAuthors()) // We only add the authors if there are any specified
            this.elements.add(new KeyTextListContentElement(this, paddingX, paddingY, MOD_AUTHORS_KEY.getString(), holder.getModAuthors()).setValueColor(PaCoColor.color(160, 160, 160)));
        if (holder.hasModCredits())
            this.elements.add(new KeyTextListContentElement(this, paddingX, paddingY, Services.PLATFORM.getPlatformName().equals("Forge") ? MOD_CREDITS_KEY.getString() : MOD_CONTRIBUTORS_KEY.getString(), holder.getModCredits()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, MOD_LICENSE_KEY.getString(), holder.getModLicense()).setValueColor(PaCoColor.color(160, 160, 160)));
        this.elements.add(new EmptyContentElement(this, paddingY));

        // If the height of the elements is greater than the panel size we flag as "needs scroll bar" and recalculate the elements with the new dimensions
        if (!this.hasScrollBar() && this.getContentHeight() > this.getScreen().contentMenuHeight) {
            this.hasScrollBar = true;
            this.posX += 8;
            this.width -= 8;
            this.buildContentPanel(holder);
            // We add the slider to the screen
            int heightPadding = PaCoScreen.PADDING_FOUR;
            int sliderHeight = this.getHeight() - heightPadding;
            int posX = this.getScreen().modsPanelWidth + PaCoScreen.PADDING_FOUR;
            int posY = this.getScreen().contentMenuHeightStart + heightPadding / 2;
            this.getScreen().setContentScrollBar(
                    new PaCoVerticalSlider(posX, posY, 6, sliderHeight, 0, (this.getContentHeight() - this.getScreen().contentMenuHeight), 0, 1)
                            .setSilent(true)
                            .setTextHidden(true)
                            .setHandleSize(8, Math.max(8, this.getHeight() - (this.getContentHeight() - this.getHeight()) - PaCoScreen.PADDING_FOUR))
                            .setSliderTexture(PaCoScreen.TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                            .setHandleTexture(PaCoScreen.TEXTURE, 12, 54, 20, 54, 8, 18, 1)
            );
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
        int scrollOffset = 0;
        if (this.getScreen().contentScrollBar != null)
            scrollOffset = (int) Math.round(this.getScreen().contentScrollBar.getValue());
        return this.posY - scrollOffset;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
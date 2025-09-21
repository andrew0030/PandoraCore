package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements.*;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaCoContentPanelManager {
    // Just a boolean that can be toggled to get some debug overlays inside the content panel.
    public static final boolean DEBUG_MODE = false;
    // Content Panel | With Active Mod
    public static final Component MOD_VERSION_KEY = Component.translatable("gui.pandora_core.paco.content.mod.version.key");
    public static final Component MOD_WARNING_KEY = Component.translatable("gui.pandora_core.paco.content.mod.warning.key");
    public static final Component MOD_UPDATE_KEY = Component.translatable("gui.pandora_core.paco.content.mod.update.key");
    public static final Component MOD_UPDATE_PAGE = Component.translatable("gui.pandora_core.paco.content.mod.update.page");
    public static final Component MOD_DESCRIPTION_KEY = Component.translatable("gui.pandora_core.paco.content.mod.description.key");
    public static final Component MOD_AUTHORS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.authors.key");
    public static final Component MOD_CREDITS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.credits.key");
    public static final Component MOD_CONTRIBUTORS_KEY = Component.translatable("gui.pandora_core.paco.content.mod.contributors.key");
    public static final Component MOD_LICENSE_KEY = Component.translatable("gui.pandora_core.paco.content.mod.license.key");
    // Content Panel | No Active Mod
    // TODO
    private final List<BaseContentElement> elements = new ArrayList<>();
    private final Set<ComponentElement> componentElements = new HashSet<>();
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

    // TODO maybe add a "Link(s):" section that has a homepage/issue tracker entry that can be clicked to open the corresponding pages
    // TODO on Fabric some "lib mods" don't have a description or author entry, so maybe add checks for all entries that don't have a check yet
    public void buildContentPanel(ModDataHolder holder) {
        this.clearElements();
        int paddingX = PaCoScreen.PADDING_FOUR;
        int paddingY = 8;
        this.elements.add(new BackgroundContentElement(this));
        this.elements.add(new BannerContentElement(this, 0, -this.getContentHeight())); // Since the banner is the second element added, moving it up by height moves it to 0
        this.elements.add(new TitleContentElement(this, paddingX, -16, holder.getModName()));
        this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, MOD_VERSION_KEY.getString(), holder.getModVersion()).setValueColor(PaCoColor.color(160, 160, 160)));
        if (holder.hasModWarnings()) // We only add warnings if there are any
            this.elements.add(new KeyTextListContentElement(this, paddingX, paddingY, MOD_WARNING_KEY.getString(), holder.getModWarnings().stream().map(Component::getString).toList(), "• ").setValueColor(PaCoScreen.SOFT_RED_TEXT_COLOR));
        if (holder.isOutdated()) {
            UpdateInfo updateInfo = holder.getUpdateInfo().get();
            String type = updateInfo.getType() != null ? updateInfo.getType().getDisplayName().getString() : "";
            String value = String.format("%s → %s [%s]", holder.getModVersion(), updateInfo.getNewVersion(), type);
            this.elements.add(new KeyTextContentElement(this, paddingX, paddingY, MOD_UPDATE_KEY.getString(), value).setValueColor(PaCoColor.color(160, 160, 160)));
            if (updateInfo.getDownloadURL() != null) {
                Component clickable = MOD_UPDATE_PAGE.copy()
                        .withStyle(style -> style
                                .withColor(ChatFormatting.BLUE)
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, updateInfo.getDownloadURL().toString()))
                        );
                ComponentElement element = new ComponentElement(this, paddingX + 13, 0, clickable);
                this.componentElements.add(element);
                this.elements.add(element);
            }
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
        this.componentElements.clear();
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

    public Set<ComponentElement> getComponentElements() {
        return this.componentElements;
    }
}
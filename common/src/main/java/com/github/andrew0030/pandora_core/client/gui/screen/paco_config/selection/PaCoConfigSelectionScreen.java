package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.ConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.sliders.FocusRectangleMode;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.IConfigManager;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// TODO probably maybe remove this class and handle selection through the main config screen somehow
public class PaCoConfigSelectionScreen extends Screen {
    // Some generic UI stuff
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    // Config management stuff
    private final Collection<IConfigManager> managers;
    private final List<ConfigEntry> visibleEntries = new ArrayList<>();
    // Screens for navigation
    public final TitleScreen titleScreen;
    public final Screen previousScreen;
    // Widgets
    public PaCoSlider entriesScrollBar;
    // Misc
    public int menuHeight;
    public int menuHeightStart;
    public int menuHeightStop;
    public int menuWidthStart;
    public int menuWidth;
    public int entriesHeight;
    public int entriesHandleHeight;

    public PaCoConfigSelectionScreen(Collection<IConfigManager> managers, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        super(Component.empty()); // TODO: Add a proper config screen title (maybe the node name?)

        this.managers = managers;
        this.titleScreen = titleScreen;
        this.previousScreen = previousScreen;

        // If there is a title screen it flags it to cancel element rendering (we only want the background)
        if (titleScreen != null)
            ((IPaCoModifyTitleScreen) titleScreen).pandoraCore$hideElements(true);
    }

    /**
     * Used to initialize the fields in this class.<br/>
     * This is mainly a method because some of the fields need to be refreshed, and calling this method does that.
     */
    private void fieldInit() {
        // Navigation Panel
//        this.navMenuHeight = this.height - 40;
//        this.navMenuHeightStart = (this.height - this.navMenuHeight) / 2;
//        this.navMenuHeightStop = this.navMenuHeightStart + this.navMenuHeight;
        int navMenuWidth = 150;
//        this.navMenuWidthStart = PaCoGuiUtils.PADDING_TWO;
//        this.navMenuEntriesHeight = this.navMenuHeight - 50; // TODO: 25 is a placeholder the the nav panel island that will be added later
//        this.navMenuEntriesStart = this.navMenuHeightStart + 50;

        // Config Entries Panel
        this.menuHeight = this.height - 40;
        this.menuHeightStart = (this.height - (this.menuHeight + 20)) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
//        this.menuWidth = Math.min(this.width - PADDING_TWO * 2, Math.round(this.menuHeight * 2.4F)) - 100;
        this.menuWidth = (this.width - PaCoGuiUtils.PADDING_TWO * 2) - navMenuWidth - PaCoGuiUtils.PADDING_TWO;
//        this.menuWidthStart = (this.width - this.menuWidth) / 2;
        this.menuWidthStart = ((this.width + navMenuWidth + PaCoGuiUtils.PADDING_TWO - this.menuWidth) / 2);
//        this.entriesHeight = this.populateEntries(false);
        this.entriesHeight = this.populateEntries(false);
        this.entriesHandleHeight = Math.max(8, this.menuHeight - (this.entriesHeight - this.menuHeight) - PaCoGuiUtils.PADDING_FOUR);
    }

    @Override
    protected void init() {
        /* Compatibility */
        if (this.titleScreen != null) {
            // Technically this isn't needed when the title screen renders a panorama, however when it has
            // a static image, due to mods like "PackMenu" we need this to update the dimensions on resize
            this.titleScreen.width = this.width;
            this.titleScreen.height = this.height;
        }

        /* Field Init */
        // Note: We need to remove the scroll bar before we generate the widgets to avoid position issues
        this.entriesScrollBar = null;
        this.fieldInit();

        /* Adding Widgets */
        // Scroll Bar (Slider)
        if (this.entriesHeight > this.menuHeight) { // We only add it if its needed
            this.entriesScrollBar = new PaCoVerticalSlider(this.menuWidthStart + PaCoGuiUtils.PADDING_TWO, this.menuHeightStart + PaCoGuiUtils.PADDING_TWO, 6, this.menuHeight - PaCoGuiUtils.PADDING_FOUR, 0, (this.entriesHeight - this.menuHeight), 0, 1)
                    .setSilent(true)
                    .setTextHidden(true)
//                    .setNarrationMessage(SCROLLBAR) //TODO add narration
                    .setHandleSize(8, this.entriesHandleHeight)
                    .setFocusReactangleMode(FocusRectangleMode.HANDLE_CENTER)
                    .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                    .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            this.addWidget(this.entriesScrollBar);
        }
        // Entry Widgets
        // TODO maybe move/change this so rendering and clicking are both handled by MC?
        this.visibleEntries.forEach(element -> this.addWidget(element.getWidget()));
    }

    @Override
    public void tick() {
        // Technically this isn't needed when the title screen renders a panorama, however when it has
        // animated content, due to mods like "PackMenu" we need this to ensure the content stays animated
        if (this.titleScreen != null) {
            // We set the current screen to the title screen just before rendering it
            // in order to keep the game state as close as possible to what is expected
            boolean isMinecraftNotNull = this.minecraft != null;
            if (isMinecraftNotNull) this.minecraft.screen = this.titleScreen;
            this.titleScreen.tick();
            if (isMinecraftNotNull) this.minecraft.screen = this;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Renders the title screen panorama/background
        if (this.titleScreen != null) {
            // We set the current screen to the title screen just before rendering it
            // in order to keep the game state as close as possible to what is expected
            boolean isMinecraftNotNull = this.minecraft != null;
            if (isMinecraftNotNull) this.minecraft.screen = this.titleScreen;
            this.titleScreen.render(graphics, mouseX, mouseY, partialTick);
            if (isMinecraftNotNull) this.minecraft.screen = this;
        }

        // Background Blur and Gradient
        RenderSystem.disableDepthTest(); // Needed so it works if chat is rendering.
        PaCoGuiUtils.blurScreen(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.fillGradient(0, 0, this.width, this.height, PaCoColor.color(83, 16, 16, 16), PaCoColor.color(67, 16, 16, 16));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Entries Panel Top/Bottom Bars
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStart - 4, this.menuWidth, 4, 1, 18, 18, 0, 36);
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStop, this.menuWidth, 4, 1, 18, 18, 0, 36);
        // Entries Panel
        this.renderEntriesPanel(graphics, mouseX, mouseY, partialTick);

        // Debug Outline
//        PaCoGuiUtils.renderBoxWithRim(graphics, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight, null, PaCoColor.color(255, 40, 40), 1);
    }

    protected void renderEntriesPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Enables GLScissors
        PaCoGuiUtils.enableScissor(graphics, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight);
        graphics.pose().pushPose();

        // Panel Background
        RenderSystem.enableBlend();
        boolean hasEntriesScrollBar = this.entriesScrollBar != null;
        int posX = hasEntriesScrollBar ? this.menuWidthStart + 10 : this.menuWidthStart + PaCoGuiUtils.PADDING_TWO;
        int width = hasEntriesScrollBar ? this.menuWidth - 10 - PaCoGuiUtils.PADDING_TWO : this.menuWidth - (PaCoGuiUtils.PADDING_TWO * 2);
        graphics.blitRepeating(TEXTURE, posX, this.menuHeightStart, width, this.menuHeight, 0, 122, 48, 48);
        // Renders the Mods Panel Scroll Bar
        if (this.entriesScrollBar != null) this.entriesScrollBar.render(graphics, mouseX, mouseY, partialTick);
        // Renders all the widgets attached to entries
        this.visibleEntries.forEach(entry -> entry.render(graphics, mouseX, mouseY, partialTick));

        // Config entry gradients
        RenderSystem.enableBlend();
        if (this.entriesScrollBar != null && this.menuHeight >= 50) {
            RenderSystem.disableDepthTest();
            int roundedVal = (int) Math.round(this.entriesScrollBar.getValue());
            // Top Gradient
            if (roundedVal > 0) {
                int gradientHeight = Math.min(25, roundedVal);
                graphics.blitRepeating(TEXTURE, posX, this.menuHeightStart, width, gradientHeight, 25, 122 - gradientHeight, 25, gradientHeight);
            }
            // Bottom Gradient
            int maxVal = this.entriesHeight - this.menuHeight;
            if (roundedVal < maxVal) {
                int gradientHeight = Math.min(25, maxVal - roundedVal);
                graphics.blitRepeating(TEXTURE, posX, this.menuHeightStart + this.menuHeight - gradientHeight, width, gradientHeight, 0, 97, 25, gradientHeight);
            }
            RenderSystem.enableDepthTest();
        }

        // Disables GLScissors
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        // If there was a title screen we make the elements visible
        if (this.titleScreen != null)
            ((IPaCoModifyTitleScreen) this.titleScreen).pandoraCore$hideElements(false);
        // We return to the most relevant screen
        if (this.previousScreen != null) {
            Minecraft.getInstance().setScreen(this.previousScreen);
        } else if (this.titleScreen != null) {
            Minecraft.getInstance().setScreen(this.titleScreen);
        } else {
            super.onClose();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Entries Panel Scroll
        if (this.entriesScrollBar != null && PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight)) {
            int maxVal = this.entriesHeight - this.menuHeight;
            int pixelStep = (int) (maxVal * 0.12); // Modify the value by 12%
            pixelStep = Mth.clamp(pixelStep, 5, 30); // Ensures that the step size is within 5-30
            int newValue = (int) (this.entriesScrollBar.getValue() - (delta * pixelStep));
            newValue = Mth.clamp(newValue, 0, maxVal);
            this.entriesScrollBar.setValue(newValue);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // NOTE: We need to call super early so the click logic runs on the current state of the UI before entries move
        boolean clickedWidget = super.mouseClicked(mouseX, mouseY, button);
        if (!clickedWidget) return false; // If no widget was clicked we don't move entries
        for (ConfigEntry entry : this.visibleEntries) {
//            if (this.isMouseInEntriesBounds(mouseX, mouseY)) {
                if (!entry.isHovered())
                    continue;
                entry.onPress();
//            }
        }
        return true;
    }

    private int populateEntries(boolean hasEntriesScrollBar) {
        // Clears previous entries to avoid potential duplicates
        this.visibleEntries.clear();
        // Config Entries Panel
        int entriesStartY = this.menuHeightStart;
        int entryCurrentY = entriesStartY + PaCoGuiUtils.PADDING_TWO;
        int entryX = this.menuWidthStart + PaCoGuiUtils.PADDING_TWO;
        int entryWidth = this.menuWidth - (PaCoGuiUtils.PADDING_TWO * 2);
        int entryHeight = 16;
        int spacing = PaCoGuiUtils.PADDING_TWO;
        // If the config entries panel has a scroll bar we need to offset the entries
        if (hasEntriesScrollBar) {
            entryX += 8;
            entryWidth -= 8;
        }

        // Performs the recursive traversal from the root node
        AtomicInteger finalY = new AtomicInteger(entryCurrentY);

        this.managers.forEach(manager -> {
            this.visibleEntries.add(new ConfigEntry(this, manager, finalY.get(), entryHeight, hasEntriesScrollBar));
            finalY.addAndGet(entryHeight + spacing);
        });

        int entriesHeight = finalY.get() - entriesStartY;
        // If there are too many entries to display without a scroll bar we recalculate
        // NOTE: We need to check if the entries don't have a scroll bar already to avoid an infinity loop
        if (!hasEntriesScrollBar && entriesHeight > this.menuHeight)
            return this.populateEntries(true);
        // The total height the entries fill
        return entriesHeight;
    }
}

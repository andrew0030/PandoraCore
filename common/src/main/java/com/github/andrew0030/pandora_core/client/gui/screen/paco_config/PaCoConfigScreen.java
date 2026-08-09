package com.github.andrew0030.pandora_core.client.gui.screen.paco_config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeBuilder;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.sliders.FocusRectangleMode;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

public class PaCoConfigScreen extends Screen {
    // Some generic UI stuff
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final int PADDING_ONE = 1;
    public static final int PADDING_TWO = 2;
    public static final int PADDING_FOUR = 4;
    // Config management stuff
    private final List<BaseConfigEntry> configEntries = new ArrayList<>();
    private final PaCoConfigManager manager;
    private final ConfigTreeNode currentNode;
    // Screens for navigation
    public final TitleScreen titleScreen;
    private final Screen previousScreen;
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
    // Post-processing shader parameters
    private final Map<String, Object> parameters;

    public PaCoConfigScreen(PaCoConfigManager manager, ConfigTreeNode currentNode, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        super(Component.empty()); // TODO: Add a proper config screen title (maybe the node name?)

        this.manager = manager;
        this.currentNode = currentNode;
        this.titleScreen = titleScreen;
        this.previousScreen = previousScreen;
        this.parameters = new HashMap<>();
        // If there is a title screen it flags it to cancel element rendering (we only want the background)
        if (titleScreen != null)
            ((IPaCoModifyTitleScreen) titleScreen).pandoraCore$hideElements(true);
    }

    public PaCoConfigScreen(PaCoConfigManager manager, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        this(manager, ConfigTreeBuilder.buildTree(manager.getAnnotationHandler().getConfigDataHolders()), titleScreen, previousScreen);
    }

    /**
     * Used to initialize the fields in this class.<br/>
     * This is mainly a method because some of the fields need to be refreshed, and calling this method does that.
     */
    private void fieldInit() {
        this.menuHeight = this.height - 40;
        this.menuHeightStart = (this.height - (this.menuHeight + 20)) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
        this.menuWidth = Math.min(this.width - PADDING_TWO * 2, Math.round(this.menuHeight * 2.4F)) - 100;
        this.menuWidthStart = (this.width - this.menuWidth) / 2;
        this.entriesHeight = this.populateEntries(false);
        this.entriesHandleHeight = Math.max(8, this.menuHeight - (this.entriesHeight - this.menuHeight) - PADDING_FOUR);
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
            this.entriesScrollBar = new PaCoVerticalSlider(this.menuWidthStart + PADDING_TWO, this.menuHeightStart + PADDING_TWO, 6, this.menuHeight - PADDING_FOUR, 0, (this.entriesHeight - this.menuHeight), 0, 1)
                    .setSilent(true)
                    .setTextHidden(true)
//                    .setNarrationMessage(SCROLLBAR) //TODO add narration
                    .setHandleSize(8, this.entriesHandleHeight)
                    .setFocusReactangleMode(FocusRectangleMode.HANDLE_CENTER)
                    .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                    .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            this.addWidget(this.entriesScrollBar);
        }
        // TODO maybe move/change this so rendering and clicking are both handled by MC?
        // All the widgets attached to config entries
        this.configEntries.forEach(element -> element.getWidgets().forEach(this::addWidget));
    }

    @Override
    public void tick() {
        this.configEntries.forEach(BaseConfigEntry::tick);
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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
        this.renderBlurredBackground(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.fillGradient(0, 0, this.width, this.height, PaCoColor.color(83, 16, 16, 16), PaCoColor.color(67, 16, 16, 16));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Top Bar
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStart - 4, this.menuWidth, 4, 1, 18, 18, 0, 36);

        // Bottom Bar
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
        int posX = hasEntriesScrollBar ? this.menuWidthStart + 10 : this.menuWidthStart + PADDING_TWO;
        int width = hasEntriesScrollBar ? this.menuWidth - 10 - PADDING_TWO : this.menuWidth - (PADDING_TWO * 2);
        graphics.blitRepeating(TEXTURE, posX, this.menuHeightStart, width, this.menuHeight, 0, 122, 48, 48);
        // Renders the Mods Panel Scroll Bar
        if (this.entriesScrollBar != null) this.entriesScrollBar.render(graphics, mouseX, mouseY, partialTick);
        // Renders all the widgets attached to entries
        this.configEntries.forEach(entry -> entry.render(graphics, mouseX, mouseY, partialTick));

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

        // Renders the entry tooltip after scissors
        this.renderTooltip(graphics, mouseX, mouseY, partialTick);
    }

    /** Renders the tooltip and its gradient */
    private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int hoveredIdx = -1;
        int focusedIdx = -1;
        for (int i = 0; i < this.configEntries.size(); i++) {
            BaseConfigEntry entry = this.configEntries.get(i);
            // Checks for hovered entries
            if (entry.isHovered()) hoveredIdx = i;
            // Checks for focused entries (only stores the first found one)
            else if (entry.isFocused() && focusedIdx == -1) focusedIdx = i;
        }
        // Prioritizes hover over focus if both exist
        int activeIdx = (hoveredIdx != -1) ? hoveredIdx : focusedIdx;
        // Renders the tooltip at the active index, if there is one
        if (activeIdx != -1) {
            BaseConfigEntry entry = this.configEntries.get(activeIdx);
            // Renders the tooltip
            entry.renderTooltip(graphics, mouseX, mouseY, partialTick);
            // Calculates the final Y position for the tooltip gradient
            boolean renderBelow = entry.renderTooltipBelow();
            int posY = renderBelow ? entry.getY() + entry.getHeight() + entry.getTooltipHeight() : entry.getY() - entry.getTooltipHeight() - 25;
            int u = renderBelow ? 25 : 0;
            // Renders the tooltip gradient // TODO gradient shouldn't render when there aren't entries under it
            PaCoGuiUtils.enableScissor(graphics, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight);
            graphics.pose().pushPose();
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            graphics.blitRepeating(
                    PaCoScreen.TEXTURE,   // The texture
                    entry.getX(), posY,   // Position to render at
                    entry.getWidth(), 25, // Size to render
                    u, 97,                // UV coordinates on texture
                    25, 25                // Size on texture
            );
            graphics.pose().popPose();
            graphics.disableScissor();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        // Handles returning to previous Screen if needed
        if (this.previousScreen != null) {
            if (this.titleScreen != null && !(this.previousScreen instanceof PaCoConfigScreen))
                ((IPaCoModifyTitleScreen) this.titleScreen).pandoraCore$hideElements(false);
            Minecraft.getInstance().setScreen(this.previousScreen);
        } else if (this.titleScreen != null) {
            ((IPaCoModifyTitleScreen) this.titleScreen).pandoraCore$hideElements(false);
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

    private void renderBlurredBackground(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // TODO: add config to adjust blurriness and fade in time
        // Map Approach
        this.parameters.put("radius", 5.0F);
        // Uniform Holder Approach
        PASS0_MUL.get().set(1.0F);
        PASS1_MUL.get().set(0.5f);
        PASS2_MUL.get().set(0.25f);

        PaCoPostShaders.PACO_BLUR.processPostChain(partialTick, this.parameters);
        minecraft.getMainRenderTarget().bindWrite(false);
    }

    /**
     * Loops over all {@link ConfigTreeNode} instances, that should
     * be displayed in the current menu, and adds them to the screen
     *
     * @return The total height of all the entries
     */
    private int populateEntries(boolean hasScrollBar) {
        this.configEntries.clear();

        int startY = this.menuHeightStart;
        int currentY = startY + PADDING_TWO;
        int entryX = this.menuWidthStart + PADDING_FOUR;
        int entryWidth = this.menuWidth - (PADDING_FOUR * 2);
        int spacing = PADDING_TWO;
        int entryHeight = 16; // TODO maybe allow modifying this within the entries?
        if (hasScrollBar) {
            entryX += 8;
            entryWidth -= 8;
        }

        for (ConfigTreeNode child : this.currentNode.getChildren()) {

            // TODO make sure this cant be null
            ConfigDataHolder holder = child.getDataHolder();
            BaseConfigEntry element = holder.getConfigEntryFactory().create(this, child, entryX, currentY, entryWidth, entryHeight);

            this.configEntries.add(element);
            currentY += entryHeight + spacing;
        }

        // Removes the trailing spacing if there was at least one entry
//        if (!this.configEntries.isEmpty())
//            currentY -= spacing;

        // If there are too many entries to display without a scroll bar we recalculate
        // the entries but this time shrinking them to fit the scroll bar
        // Note: We need to check if there is a scroll bar to prevent infinite recursion
        if (!hasScrollBar && currentY - startY > this.menuHeight)
            this.populateEntries(true);

        return currentY - startY;
    }

    /** @return The {@link PaCoConfigManager} instance associated to this {@link PaCoConfigScreen}. */
    public PaCoConfigManager getManager() {
        return this.manager;
    }

    /** @return Whether the mouse is within the entries panel */
    public boolean isMouseInEntriesBounds(double mouseX, double mouseY) {
        return PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight);
    }
}
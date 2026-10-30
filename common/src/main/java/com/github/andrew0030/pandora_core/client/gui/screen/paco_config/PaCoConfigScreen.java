package com.github.andrew0030.pandora_core.client.gui.screen.paco_config;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.config.*;
import com.github.andrew0030.pandora_core.client.gui.edit_boxes.ConfigSearchEditBox;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.CategoryEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeBuilder;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.sliders.FocusRectangleMode;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.IConfigManager;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PaCoConfigScreen extends Screen {
    // Some generic UI stuff
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final int TOOLTIP_GRADIENT_SIZE = 10;
    // Navigation Panel
    public static final Component SEARCH = Component.translatable("gui.pandora_core.config.search");
    public static final Component NO_MATCHES = Component.translatable("gui.pandora_core.config.no_matches");
    // Config management stuff
    private final List<ConfigEntryNavigationButton> navButtons = new ArrayList<>();
    private final List<BaseConfigEntry<?>> visibleEntries = new ArrayList<>();
    private final IConfigManager manager;
    private final ConfigTreeNode currentNode;
    private final ConfigTreeNode rootNode;
    // Screens for navigation
    public final TitleScreen titleScreen;
    public final Screen previousScreen; // TODO change accessibility by altering how the category entry accesses these
    // Widgets
    public ConfigSearchEditBox searchBox;
    public PaCoSlider entriesScrollBar;
    public ConfigActionButton backButton;
    // Misc
    public static final int DARK_GRAY_TEXT_COLOR = PaCoColor.color(130, 130, 130);
    public static final int SOFT_RED_TEXT_COLOR = PaCoColor.color(255, 90, 100);
    public static final int DARK_RED_TEXT_COLOR = PaCoColor.color(235, 74, 74);
    public int navMenuHeight;
    public int navMenuHeightStart;
    public int navMenuHeightStop;
    public int navMenuWidth;
    public int navMenuWidthStart;
    public int navMenuEntriesHeight;
    public int navMenuEntriesStart;

    public int menuHeight;
    public int menuHeightStart;
    public int menuHeightStop;
    public int menuWidthStart;
    public int menuWidth;
    public int entriesHeight;
    public int entriesHandleHeight;

    // TODO maybe pass along previous node for better performance?
    public PaCoConfigScreen(IConfigManager manager, ConfigTreeNode currentNode, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        super(Component.empty()); // TODO: Add a proper config screen title (maybe the node name?)

        this.manager = manager;
        this.currentNode = currentNode;
        this.titleScreen = titleScreen;
        this.previousScreen = previousScreen;

        // Traverses up the tree to find the root node
        ConfigTreeNode root = this.currentNode;
        while (root.getParent() != null)
            root = root.getParent();
        this.rootNode = root;

        // If there is a title screen it flags it to cancel element rendering (we only want the background)
        if (titleScreen != null)
            ((IPaCoModifyTitleScreen) titleScreen).pandoraCore$hideElements(true);
    }

    public PaCoConfigScreen(IConfigManager manager, @Nullable TitleScreen titleScreen, @Nullable Screen previousScreen) {
        this(manager, ConfigTreeBuilder.buildTree(manager.getDataHolders()), titleScreen, previousScreen);
    }

    /**
     * Used to initialize the fields in this class.<br/>
     * This is mainly a method because some of the fields need to be refreshed, and calling this method does that.
     */
    private void fieldInit() {
        // Navigation Panel
        this.navMenuHeight = this.height - 40;
        this.navMenuHeightStart = (this.height - this.navMenuHeight) / 2;
        this.navMenuHeightStop = this.navMenuHeightStart + this.navMenuHeight;
        this.navMenuWidth = 150;
        this.navMenuWidthStart = PaCoGuiUtils.PADDING_TWO;
        this.navMenuEntriesHeight = this.navMenuHeight - 50; // TODO: 50 is a placeholder the the nav panel island that will be added later
        this.navMenuEntriesStart = this.navMenuHeightStart + 50;

        // Config Entries Panel
        this.menuHeight = this.height - 40;
        this.menuHeightStart = (this.height - (this.menuHeight + 20)) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
//        this.menuWidth = Math.min(this.width - PADDING_TWO * 2, Math.round(this.menuHeight * 2.4F)) - 100;
        this.menuWidth = (this.width - PaCoGuiUtils.PADDING_TWO * 2) - navMenuWidth - PaCoGuiUtils.PADDING_TWO;
//        this.menuWidthStart = (this.width - this.menuWidth) / 2;
        this.menuWidthStart = ((this.width + navMenuWidth + PaCoGuiUtils.PADDING_TWO - this.menuWidth) / 2);
//        this.entriesHeight = this.populateEntries(false);
        this.entriesHeight = this.populateEntriesAndNavigation(false);
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
        // Search Edit Box
        String activeSearchText = this.searchBox != null ? this.searchBox.getValue() : null; // If there is text we grab it before the EditBox is discarded.
        this.searchBox = new ConfigSearchEditBox(this.font, this.navMenuWidthStart + PaCoGuiUtils.PADDING_TWO, this.navMenuHeightStart, this.navMenuWidth - (PaCoGuiUtils.PADDING_TWO * 2), 14, Component.literal("TODO"), this); //TODO narration
        this.searchBox.setMaxLength(50);
        this.searchBox.setHint(SEARCH);
        this.searchBox.setTextColor(DARK_GRAY_TEXT_COLOR);
//        this.searchBox.setBackgroundHidden(true);
//        this.searchBox.setRimHidden(true);
        this.searchBox.setForceLineIndicator(true);
        this.searchBox.setMidpointCharSelection(true);
        this.addRenderableWidget(this.searchBox);
        // Navigation Widgets
        this.navButtons.forEach(this::addWidget);
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
        this.visibleEntries.forEach(element -> element.getWidgets().forEach(this::addWidget));


        // TODO add all actions
        // Menu Action Buttons
        this.addRenderableWidget(this.backButton = new ConfigBackButton(this));
        this.addRenderableWidget(new ConfigResetButton(this));
        this.addRenderableWidget(new ConfigSaveButton(this));

        // On resize if there was text in the search bar we apply it to the new one, which will update everything
        if (activeSearchText != null)
            this.searchBox.setValue(activeSearchText);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
        this.visibleEntries.forEach(BaseConfigEntry::tick);
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

        // Navigation Panel Top/Bottom Bars
        graphics.blitNineSliced(TEXTURE, this.navMenuWidthStart, this.navMenuHeightStart - 3, this.navMenuWidth, 3, 1, 18, 18, 0, 36);
        graphics.blitNineSliced(TEXTURE, this.navMenuWidthStart, this.navMenuHeightStop, this.navMenuWidth, 3, 1, 18, 18, 0, 36);
        // Navigation Panel
        this.renderNavPanel(graphics, mouseX, mouseY, partialTick);

        // Entries Panel Top/Bottom Bars
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStart - 4, this.menuWidth, 4, 1, 18, 18, 0, 36);
        graphics.blitNineSliced(TEXTURE, this.menuWidthStart, this.menuHeightStop, this.menuWidth, 4, 1, 18, 18, 0, 36);
        // Entries Panel
        this.renderEntriesPanel(graphics, mouseX, mouseY, partialTick);


        super.render(graphics, mouseX, mouseY, partialTick);
        // Debug Outline
//        PaCoGuiUtils.renderBoxWithRim(graphics, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight, null, PaCoColor.color(255, 40, 40), 1);
    }

    protected void renderNavPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        graphics.blitRepeating(TEXTURE, this.navMenuWidthStart + PaCoGuiUtils.PADDING_TWO, this.navMenuEntriesStart, this.navMenuWidth - PaCoGuiUtils.PADDING_TWO * 2, this.navMenuEntriesHeight, 0, 122, 48, 48);


        this.navButtons.forEach(button -> button.render(graphics, mouseX, mouseY, partialTick));


        // TODO replace later with the navigation panel
        PaCoGuiUtils.renderBoxWithRim(graphics, this.navMenuWidthStart + PaCoGuiUtils.PADDING_TWO, this.navMenuHeightStart, this.navMenuWidth - PaCoGuiUtils.PADDING_TWO * 2, this.navMenuEntriesStart - this.navMenuHeightStart, null, PaCoColor.color(255, 40, 40), 1);
        // TODO remove later when config entry buttons are added
//        PaCoGuiUtils.renderBoxWithRim(graphics, this.menuWidthStart + PaCoGuiUtils.PADDING_TWO, this.menuHeightStop + 6, this.menuWidth - PaCoGuiUtils.PADDING_TWO * 2, 18, null, PaCoColor.color(255, 40, 40), 1);
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

        // No Entries Message
        if (this.searchBox.isHidingAllEntries()) {
            int yPadding = 3;
            Pair<Integer, Integer> dimensions = PaCoGuiUtils.drawCenteredWordWrapWithDimensions(graphics, this.font, NO_MATCHES, this.menuWidthStart + PaCoGuiUtils.PADDING_TWO, this.menuHeightStart + PaCoGuiUtils.PADDING_THREE + yPadding, this.menuWidth - (PaCoGuiUtils.PADDING_TWO * 2), SOFT_RED_TEXT_COLOR, true);
            RenderSystem.enableBlend();
            graphics.blitRepeating(TEXTURE, this.menuWidthStart + PaCoGuiUtils.PADDING_TWO, this.menuHeightStart + PaCoGuiUtils.PADDING_TWO, this.menuWidth - (PaCoGuiUtils.PADDING_TWO * 2), dimensions.getSecond() + yPadding * 2 + 1, 48, 122, 48, 48);
        }

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

        // Renders the entry tooltip after scissors
        this.renderTooltip(graphics, mouseX, mouseY, partialTick);
    }

    /** Renders the tooltip and its gradient */
    private void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int hoveredIdx = -1;
        int focusedIdx = -1;
        for (int i = 0; i < this.visibleEntries.size(); i++) {
            BaseConfigEntry<?> entry = this.visibleEntries.get(i);
            // Checks for hovered entries
            if (entry.isHovered()) hoveredIdx = i;
            // Checks for focused entries (only stores the first found one)
            else if (entry.isFocused() && focusedIdx == -1) focusedIdx = i;
        }
        boolean isMouseHoveringAnyEntry = (hoveredIdx != -1);
        // If an entry is hovered, resets all other entries hover time (needed so focused entries get their time reset)
        if (isMouseHoveringAnyEntry) {
            for (int i = 0; i < this.visibleEntries.size(); i++) {
                if (i == hoveredIdx) continue;
                BaseConfigEntry<?> entry = this.visibleEntries.get(i);
                entry.resetHoverTime();
            }
        }
        // Prioritizes hover over focus if both exist
        int activeIdx = isMouseHoveringAnyEntry ? hoveredIdx : focusedIdx;
        // Renders the tooltip at the active index, if there is one
        if (activeIdx != -1) {
            BaseConfigEntry<?> entry = this.visibleEntries.get(activeIdx);
            // If the entry isn't within the panel's bounds we skip rendering
            if (!entry.isInBounds()) return;
            // Whether the tooltip should render
            if (!entry.shouldRenderTooltip()) return;
            // Renders the tooltip
            entry.renderTooltip(graphics, mouseX, mouseY, partialTick);
            // Calculates the final Y position for the tooltip gradient
            boolean renderBelow = entry.renderTooltipBelow();
            int posY = renderBelow ? entry.getY() + entry.getHeight() + entry.getTooltipHeight() : entry.getY() - entry.getTooltipHeight() - TOOLTIP_GRADIENT_SIZE;
            int u = renderBelow ? 75 : 50;
            int v = 97;
            int gradientHeight = TOOLTIP_GRADIENT_SIZE;
            // Renders the tooltip gradient
            if (renderBelow && this.entriesHeight < this.menuHeight) {
                int entriesBottom = this.menuHeightStart + this.entriesHeight - 2; // TODO maybe adjust 2 depending on padding
                int gradientBottom = posY + TOOLTIP_GRADIENT_SIZE;
                int overextension = entriesBottom - gradientBottom;
                if (overextension < 0) {
                    gradientHeight += overextension;
                    v += TOOLTIP_GRADIENT_SIZE - gradientHeight;
                }
            }
            if (gradientHeight > 0) {
                PaCoGuiUtils.enableScissor(graphics, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight);
                graphics.pose().pushPose();
                RenderSystem.enableBlend();
                RenderSystem.disableDepthTest();
                graphics.blitRepeating(
                        PaCoScreen.TEXTURE,               // The texture
                        entry.getX(), posY,               // Position to render at
                        entry.getWidth(), gradientHeight, // Size to render
                        u, v,                             // UV coordinates on texture
                        25, gradientHeight                // Size on texture
                );
                graphics.pose().popPose();
                graphics.disableScissor();
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // TODO maybe the PaCo screen key should also close the config screen?
//        if (PaCoKeyMappings.KEY_PACO.matches(keyCode, scanCode) && this.searchBox != null && !this.searchBox.isFocused()) {
//            this.onClose();
//            return true;
//        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            // If there is an active search we clear the search instead of closing the menu
            // TODO Probably fine tune this to exit fully if optional instant escape close is enabled!
            if (!StringUtil.isNullOrEmpty(this.searchBox.getValue())) {
                this.searchBox.setValue("");
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        // If the current node isn't the root node, we simply move up to the node's parent
        if (this.currentNode != this.rootNode && this.currentNode.getParent() != null) {
            Minecraft.getInstance().setScreen(new PaCoConfigScreen(this.manager, this.currentNode.getParent(), this.titleScreen, this.previousScreen));
        } else { // If the current node is the root, we return to the actual previous screen
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
        for (BaseConfigEntry<?> entry : this.visibleEntries) {
            if (this.isMouseInEntriesBounds(mouseX, mouseY)) {
                if (!entry.isHovered())
                    continue;
                entry.onPress();
            }
        }
        return true;
    }

    /**
     * Loops over the {@link ConfigTreeNode} tree starting from the {@code root} node.
     * <p>Creates {@link ConfigEntryNavigationButton} instances for all relevant categories, and
     * creates {@link BaseConfigEntry} instances for all visible config entries of the current node.</p>
     *
     * @param hasEntriesScrollBar Whether the config entry panel has a scroll bar
     * @return The total height of the visible entries
     */
    private int populateEntriesAndNavigation(boolean hasEntriesScrollBar) {
        // Clears previous entries to avoid potential duplicates
        this.visibleEntries.clear();
        this.navButtons.clear();
        // Navigation Panel
        int navBaseX = this.navMenuWidthStart + PaCoGuiUtils.PADDING_TWO;
        int navWidth = this.navMenuWidth - (2 * PaCoGuiUtils.PADDING_TWO);
        int navHeight = ConfigEntryNavigationButton.BUTTON_HEIGHT;
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
        // Small helper object to pass along the dimensions
        LayoutContext ctx = new LayoutContext(hasEntriesScrollBar, entryHeight, spacing);
        // Performs the recursive traversal from the root node
        int finalY = this.traverseTree(this.rootNode, entryCurrentY, 0, ctx);
        int entriesHeight = finalY - entriesStartY;
        // If there are too many entries to display without a scroll bar we recalculate
        // NOTE: We need to check if the entries don't have a scroll bar already to avoid an infinity loop
        if (!hasEntriesScrollBar && entriesHeight > this.menuHeight)
            return this.populateEntriesAndNavigation(true);
        // The total height the entries fill
        return entriesHeight;
    }

    /**
     * Recursively traverses the config tree.
     * <p>Creates {@link ConfigEntryNavigationButton} instances for all relevant categories, and
     * creates {@link BaseConfigEntry} instances for all visible config entries of the current node.</p>
     *
     * @param node     The initial {@link ConfigTreeNode} that will be traversed
     * @param currentY The starting height, this is contentiously increased to track the total height
     * @param depth    The current {@code depth} that is being traversed
     * @param ctx      The {@link LayoutContext} holding the dimensions of the UI
     *
     * @return The {@code y} coordinate of the final {@link ConfigTreeNode}'s height
     */
    private int traverseTree(ConfigTreeNode node, int currentY, int depth, LayoutContext ctx) {
        boolean isInViewedCategory = node == this.currentNode;
        String searchText = this.searchBox == null ? "" : this.searchBox.getValue();
        boolean isSearching = !searchText.isEmpty();
        for (ConfigTreeNode child : node.getChildren()) {
            ConfigDataHolder<?> holder = child.getDataHolder();
            boolean isCategory = !child.isValue();
            boolean isValue = !isCategory;
            // Categories always require instantiation to build the nav menu
            // Values are only instantiated if they are in the viewed category, or while searching (we need the entries to compare them)
            if (isInViewedCategory || isCategory || isSearching) {
                BaseConfigEntry<?> entry = holder.getConfigEntryFactory().create(this, child, currentY, ctx.entryHeight(), ctx.hasEntryScrollbar());
                // If the entry is visible (in the currently viewed panel), and there is no active search, we add it to the visible entries list
                // Otherwise if there is an active search and the entry is a value, we check if it matches the search
                if ((isInViewedCategory && !isSearching) || (isSearching && isValue && entry.matchesSearch(searchText))) {
                    entry.setVisible(true);
                    this.visibleEntries.add(entry);
                    currentY += ctx.entryHeight() + ctx.spacing();
                }
                // If the config tree node is a category, we create a navigation button and attach the entry // TODO maybe make it so nav buttons don't use the entries
                if (isCategory) {
                    // Since nav button height doesn't change, we can use the list size to determine the current height
                    int navCurrentY = this.navMenuEntriesStart + ConfigEntryNavigationButton.BUTTON_PADDING + (this.navButtons.size() * (ConfigEntryNavigationButton.BUTTON_HEIGHT + ConfigEntryNavigationButton.BUTTON_PADDING));
                    // Technically this check shouldn't be needed, but in case there is ever an entry that isn't a value and also not a category I will keep this check here
                    if (entry instanceof CategoryEntry categoryEntry) {
                        ConfigEntryNavigationButton button = new ConfigEntryNavigationButton(this, categoryEntry, navCurrentY, depth);
                        entry.setNavButton(button);
                        this.navButtons.add(button);
                    }
                }
            }
            // Recursively checks the remaining tree branches
            currentY = this.traverseTree(child, currentY, depth + 1, ctx);
        }
        return currentY;
    }

    /** @return A {@link List} containing all {@link BaseConfigEntry} instances that are currently displayed. */
    public List<BaseConfigEntry<?>> getVisibleEntries() {
        return this.visibleEntries;
    }

    /** @return The {@link IConfigManager} instance associated to this {@link PaCoConfigScreen} */
    public IConfigManager getManager() {
        return this.manager;
    }

    /** @return The currently selected {@link ConfigTreeNode} */
    public ConfigTreeNode getCurrentNode() {
        return this.currentNode;
    }

    /** @return The {@code root} {@link ConfigTreeNode} of this {@link PaCoConfigScreen} */
    public ConfigTreeNode getRootNode() {
        return this.rootNode;
    }

    /** @return Whether the mouse is within the entries panel */
    public boolean isMouseInEntriesBounds(double mouseX, double mouseY) {
        return PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.menuWidthStart, this.menuHeightStart, this.menuWidth, this.menuHeight);
    }

    /** Called to update the navigation buttons list and visible entries list, as well as their corresponding UI elements. */
    public void refresh() {
        // Removes the previous widgets
        this.navButtons.forEach(this::removeWidget);
        this.visibleEntries.forEach(element -> element.getWidgets().forEach(this::removeWidget));
        if (this.entriesScrollBar != null) {
            this.removeWidget(this.entriesScrollBar);
            this.entriesScrollBar = null;
        }
        // Since there is (probably) a new number of entries we need to refresh all the fields
        // NOTE: This also clears and repopulates the lists used above to remove the old widgets
        this.fieldInit();
        // Adds new widgets based on the newly populated lists
        this.navButtons.forEach(this::addWidget);
        this.visibleEntries.forEach(element -> element.getWidgets().forEach(this::addWidget));
        if (this.entriesHeight > this.menuHeight) { // We only add it if its needed
            this.entriesScrollBar = new PaCoVerticalSlider(this.menuWidthStart + PaCoGuiUtils.PADDING_TWO, this.menuHeightStart + PaCoGuiUtils.PADDING_TWO, 6, this.menuHeight - PaCoGuiUtils.PADDING_FOUR, 0, (this.entriesHeight - this.menuHeight), 0, 1)
                .setSilent(true)
                .setTextHidden(true)
//                .setNarrationMessage(SCROLLBAR) //TODO add narration
                .setHandleSize(8, this.entriesHandleHeight)
                .setFocusReactangleMode(FocusRectangleMode.HANDLE_CENTER)
                .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            this.addWidget(this.entriesScrollBar);
        }
    }

    /**
     * A small helper object to store the panel dimensions for cleaner recursive methods.
     *
     * @param entryHeight The {@code height} of a config entry // TODO maybe make this dynamic?
     * @param spacing     The {@code padding} between config entries
     */
    private record LayoutContext(boolean hasEntryScrollbar, int entryHeight, int spacing) {}
}
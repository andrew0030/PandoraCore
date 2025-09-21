package com.github.andrew0030.pandora_core.client.gui.screen.paco_main;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.ModsFilterButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModImageManager;
import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements.ComponentElement;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.registry.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoModifyTitleScreen;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

public class PaCoScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    // Mods Panel
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    public static final Component SEARCH = Component.translatable("gui.pandora_core.paco.search");
    public static final Component NO_MATCHES = Component.translatable("gui.pandora_core.paco.no_matches");
    public static final Component NO_WARNINGS = Component.translatable("gui.pandora_core.paco.no_warnings");
    public static final Component NO_UPDATES = Component.translatable("gui.pandora_core.paco.no_updates");
    // Managers
    public final ModImageManager imageManager = new ModImageManager();
    public PaCoContentPanelManager contentPanelManager;
    private final Map<String, Object> parameters;
    // Transition Stuff
    private float fadeInProgress = 0.0F;
    private final long openTime;
    // Previous Screen Stuff
    private TitleScreen titleScreen = null;
    private Screen previousScreen = null;
    // Widgets
    private final List<Renderable> panelModButtons = new ArrayList<>();
    public PaCoEditBox searchBox;
    public ModsFilterButton filterButton;
    public PaCoSlider modsScrollBar;
    public ModButton selectedModButton;
    public PaCoSlider contentScrollBar;
    // Misc
    public static final int DARK_GRAY_TEXT_COLOR = PaCoColor.color(130, 130, 130);
    public static final int SOFT_RED_TEXT_COLOR = PaCoColor.color(255, 90, 100);
    public static final int DARK_RED_TEXT_COLOR = PaCoColor.color(235, 74, 74);
    public static final int PADDING_ONE = 1;
    public static final int PADDING_TWO = 2;
    public static final int PADDING_THREE = 3;
    public static final int PADDING_FOUR = 4;
    private static final int MOD_BUTTON_HEIGHT = 25;
    public final List<ModDataHolder> filteredMods = this.createOrderedModsList();
    public int menuHeight;
    public int contentMenuHeight;
    public int menuHeightStart;
    public int menuHeightStop;
    public int contentMenuHeightStart;
    public int contentMenuHeightStop;
    public int modsPanelWidth;
    public int contentPanelWidth;
    public int modButtonsStart;
    public int modButtonsLength;
    public int modButtonsPanelLength;
    public int modButtonWidth;
    public int modsHandleHeight;

    /** Opens the {@link PaCoScreen} directly, without any previous {@link Screen} instances. */
    public PaCoScreen() {
        super(TITLE);
        this.parameters = new HashMap<>();
        this.openTime = System.currentTimeMillis();
    }

    /**
     * Opens the {@link PaCoScreen} from within the {@link TitleScreen}.
     * The {@link TitleScreen} instance is ticked, meaning when we exit {@link PaCoScreen}, the panorama progress remains the same.
     *
     * @param titleScreen The {@link TitleScreen} that will be opened when we exist {@link PaCoScreen}
     */
    public PaCoScreen(TitleScreen titleScreen) {
        this();
        this.titleScreen = titleScreen;
        // If there is a title screen it flags it to cancel element rendering (we only want the background)
        if (titleScreen != null)
            ((IPaCoModifyTitleScreen) titleScreen).pandoraCore$hideElements(true);
    }

    /**
     * Opens the {@link PaCoScreen} from within a different mod list {@link Screen} instance, which has the {@link TitleScreen} cached internally.
     * The {@link TitleScreen} instance is ticked, meaning when we exit that {@link Screen}, the panorama progress remains the same.
     *
     * @param titleScreen    The {@link TitleScreen} stored inside {@code previousScreen}
     * @param previousScreen The {@link Screen} that will be opened when we exist {@link PaCoScreen}
     */
    public PaCoScreen(TitleScreen titleScreen, Screen previousScreen) {
        this(titleScreen);
        this.previousScreen = previousScreen;
    }

    /**
     * Used to initialize the fields in this class.<br/>
     * This is mainly a method because some of the fields need to be refreshed, and calling this method does that.
     */
    private void fieldInit() {
        this.menuHeight = this.height - 40;
        this.contentMenuHeight = this.height - 20;
        this.menuHeightStart = (this.height - this.menuHeight) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
        this.contentMenuHeightStart = (this.height - this.contentMenuHeight) / 2;
        this.contentMenuHeightStop = this.contentMenuHeightStart + this.contentMenuHeight;
        this.modsPanelWidth = Mth.floor(this.width * 0.32F);
        this.contentPanelWidth = this.width - this.modsPanelWidth - PADDING_TWO;
        this.modButtonsStart = 38;
        this.modButtonsLength = (this.filteredMods.size() * (MOD_BUTTON_HEIGHT + PADDING_ONE)) - 1;
        this.modButtonsPanelLength = this.menuHeightStop - this.modButtonsStart - PADDING_THREE;
        this.modButtonWidth = this.modsPanelWidth - (this.modButtonsLength > this.modButtonsPanelLength ? 15 : 10);
        this.modsHandleHeight = Math.max(8, this.modButtonsPanelLength - (this.modButtonsLength - this.modButtonsPanelLength) + PADDING_ONE);
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
        this.fieldInit();

        /* Adding Widgets */
        String activeSearchText = this.searchBox != null ? this.searchBox.getValue() : null; // If there is text we grab it before the EditBox is discarded.
        this.searchBox = null;
        ModsFilterButton.FilterType filterType = this.filterButton != null ? this.filterButton.getFilterType() : ModsFilterButton.FilterType.NONE;
        this.filterButton = null;
        this.panelModButtons.clear(); // We clear the list (needed because resize would cause duplicates otherwise)
        this.modsScrollBar = null;
        this.contentScrollBar = null;
        // Search Box
        this.searchBox = new PaCoEditBox(this.font, 7, this.menuHeightStart + 2, this.modsPanelWidth - 27, 14, SEARCH, this);
        this.searchBox.setMaxLength(50);
        this.searchBox.setHint(SEARCH);
        this.searchBox.setTextColor(DARK_GRAY_TEXT_COLOR);
        this.addWidget(this.searchBox);
        // Filter Button
        this.filterButton = new ModsFilterButton(this.modsPanelWidth - 18, this.menuHeightStart, this);
        this.filterButton.setFilterType(filterType);
        this.addWidget(this.filterButton);
        // Mod Buttons
        for (int i = 0; i < this.filteredMods.size(); i++) {
            ModButton modButton = new ModButton(5, this.modButtonsStart + (i * (MOD_BUTTON_HEIGHT + PADDING_ONE)), this.modButtonWidth, MOD_BUTTON_HEIGHT, this.filteredMods.get(i), this);
            if (this.selectedModButton != null && this.selectedModButton.getModDataHolder().getModId().equals(this.filteredMods.get(i).getModId())) {
                modButton.setSelected(true);
                this.selectedModButton = modButton;
            }
            this.addModToPanel(modButton);
        }
        // Scroll Bar (Slider)
        if (this.modButtonsLength > this.modButtonsPanelLength) { // We only add it if its needed
            this.modsScrollBar = new PaCoVerticalSlider(this.modsPanelWidth - 7, this.modButtonsStart + PADDING_TWO, 6, this.modButtonsPanelLength + PADDING_ONE, 0, (this.modButtonsLength - this.modButtonsPanelLength), 0, 1)
                    .setSilent(true)
                    .setTextHidden(true)
                    .setHandleSize(8, this.modsHandleHeight)
                    .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                    .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            this.addWidget(this.modsScrollBar);
        }
        // On resize if there was text in the search bar we apply it to the new one, which will update everything
        if (activeSearchText != null)
            this.searchBox.setValue(activeSearchText);

        this.contentPanelManager = new PaCoContentPanelManager(this);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
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
        long elapsed = System.currentTimeMillis() - this.openTime;
        int fadeInTime = 160; //TODO add config options for fade in time and blurriness
        this.fadeInProgress = (elapsed + partialTick) < fadeInTime ? (elapsed + partialTick) / fadeInTime : 1.0F;
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
        // TODO: if PaCo menu behaviour is weird, for example elements disappear, maybe look into re-enabling depth test.
        this.renderBlurredBackground(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.fadeInProgress);//TODO: look into this darkness fade, once fading has a config
        graphics.fillGradient(0, 0, this.width, this.height, PaCoColor.color(83, 16, 16, 16), PaCoColor.color(67, 16, 16, 16));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int slideInTime = 600; //TODO add config options for slide in time
        float slideInProgress = (elapsed + partialTick) < slideInTime ? (elapsed + partialTick) / slideInTime : 1.0F;
        // Mods Panel
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideInProgress)), 0.0F, 0.0F);
        this.renderModsPanel(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        // Content Panel
        graphics.pose().pushPose();
        graphics.pose().translate(width * (1 - Easing.CUBIC_OUT.apply(slideInProgress)), 0.0F, 0.0F);
        this.renderContentPanel(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        // Filter Button
        // The reason this isn't inside "renderModsPanel" is because the tooltip overlaps with this content panel, which causes issues.
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideInProgress)), 0.0F, 0.0F);
        this.filterButton.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
    }

    protected void renderModsPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Debug Outline
//        PaCoGuiUtils.renderBoxWithRim(graphics, 0, this.menuHeightStart, this.modsPanelWidth, this.menuHeight, null, PaCoColor.color(255, 40, 40), 1);
        // No Mods Message
        if (this.filteredMods.isEmpty() && this.searchBox != null && this.filterButton != null) {
            Component message = NO_MATCHES;
            if (!this.searchBox.isHidingAllMods()) {
                switch (this.filterButton.getFilterType()) {
                    case WARNINGS -> message = NO_WARNINGS;
                    case UPDATES -> message = NO_UPDATES;
                }
            }
            int yPadding = 4;
            Pair<Integer, Integer> dimensions = PaCoGuiUtils.drawCenteredWordWrapWithDimensions(graphics, this.font, message, 5, this.modButtonsStart + yPadding, this.modButtonWidth, SOFT_RED_TEXT_COLOR, true);
            RenderSystem.enableBlend();
            graphics.blitRepeating(TEXTURE, 5, this.modButtonsStart, this.modButtonWidth, dimensions.getSecond() + yPadding * 2, 25, 72, 25, 25);
        }
        // Search Bar
        graphics.blit(TEXTURE, 0, this.menuHeightStart, 1, 36, 5, 18);
        graphics.blit(TEXTURE, 5, this.menuHeightStart, 18, 36, 9, 18);
        graphics.blitRepeating(TEXTURE, 14, this.menuHeightStart, this.modsPanelWidth - 41, 18, 27, 36, 18, 18);
        graphics.blit(TEXTURE, this.modsPanelWidth - 27, this.menuHeightStart, 45, 36, 9, 18);
        // Bottom Bar
        if (this.modsScrollBar != null) // No scroll bar means, not enough mods to reach the "bottom"
            graphics.blitNineSliced(TEXTURE, 5, this.modButtonsStart + this.modButtonsPanelLength, this.modButtonWidth, 3, 1, 18, 18, 0, 36);
        // Renders Mod Buttons
        PaCoGuiUtils.enableScissor(graphics, 5, this.modButtonsStart, this.modButtonWidth, this.modButtonsPanelLength);
        graphics.pose().pushPose();
        for (Renderable renderable : this.panelModButtons)
            renderable.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();

        // Mod Buttons Gradients
        RenderSystem.enableBlend();
        if (this.modsScrollBar != null && this.modButtonsPanelLength >= 50) {
            RenderSystem.disableDepthTest();
            int roundedVal = (int) Math.round(this.modsScrollBar.getValue());
            // Top Gradient
            if (roundedVal > 0) {
                int gradientHeight = Math.min(25, roundedVal);
                graphics.blitRepeating(TEXTURE, 5, this.modButtonsStart, this.modButtonWidth, gradientHeight, 25, 122 - gradientHeight, 25, gradientHeight);
            }
            // Bottom Gradient
            int maxVal = this.modButtonsLength - this.modButtonsPanelLength;
            if (roundedVal < maxVal) {
                int gradientHeight = Math.min(25, maxVal - roundedVal);
                graphics.blitRepeating(TEXTURE, 5, this.modButtonsStart + this.modButtonsPanelLength - gradientHeight, this.modButtonWidth, gradientHeight, 0, 97, 25, gradientHeight);
            }
            RenderSystem.enableDepthTest();
        }

        // Note: Widgets "should" be rendered after scissors, because certain things like tooltips are weird if rendered before.
        // Renders Search Box and Filter Button
        this.searchBox.render(graphics, mouseX, mouseY, partialTick);
        // Moved filterButton render call into "render" since it needs to be rendered after the content panel to prevent some overlapping issues.
        // this.filterButton.render(graphics, mouseX, mouseY, partialTick);
        // Renders the Mods Panel Scroll Bar
        if (this.modsScrollBar != null) this.modsScrollBar.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void renderContentPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hasScrollBar = this.contentPanelManager.hasScrollBar();
        int posX = this.modsPanelWidth + (hasScrollBar ? 12 : 4);
        int width = this.contentPanelWidth - (hasScrollBar ? 10 : 2);
        // Panel Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(TEXTURE, posX, this.contentMenuHeightStart, width, this.contentMenuHeight, 0, 122, 48, 48);
        // Renders all the elements inside the content panel
        PaCoGuiUtils.enableScissor(graphics, this.contentPanelManager.getPosX(), this.contentMenuHeightStart, this.contentPanelManager.getWidth(), this.contentMenuHeight);
        graphics.pose().pushPose();
        this.contentPanelManager.renderElements(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();

        // Content Panel Gradients
        RenderSystem.enableBlend();
        if (this.contentScrollBar != null && this.contentMenuHeight >= 50) {
            RenderSystem.disableDepthTest();
            int roundedVal = (int) Math.round(this.contentScrollBar.getValue());
            // Top Gradient
            if (roundedVal > 0) {
                int gradientHeight = Math.min(25, roundedVal);
                graphics.blitRepeating(TEXTURE, this.contentPanelManager.getPosX(), this.contentMenuHeightStart, this.contentPanelManager.getWidth(), gradientHeight, 25, 122 - gradientHeight, 25, gradientHeight);
            }
            // Bottom Gradient
            int maxVal = this.contentPanelManager.getContentHeight() - this.contentMenuHeight;
            if (roundedVal < maxVal) {
                int gradientHeight = Math.min(25, maxVal - roundedVal);
                graphics.blitRepeating(TEXTURE, this.contentPanelManager.getPosX(), this.contentMenuHeightStart + this.contentMenuHeight - gradientHeight, this.contentPanelManager.getWidth(), gradientHeight, 0, 97, 25, gradientHeight);
            }
            RenderSystem.enableDepthTest();
        }
        // Renders the Content Panel Scroll Bar
        if (this.contentScrollBar != null) this.contentScrollBar.render(graphics, mouseX, mouseY, partialTick);
        // Top Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStart - 4, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
        // Bottom Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStop, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (PaCoKeyMappings.KEY_PACO.matches(keyCode, scanCode) && this.searchBox != null && !this.searchBox.isFocused()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        // Clears the Icon cache
        this.imageManager.close();
        // Handles returning to previous Screen if needed
        if (this.previousScreen != null) {
            if (this.titleScreen != null)
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) { // left-click
            for (ComponentElement element : this.contentPanelManager.getComponentElements()) {
                int textWidth = this.font.width(element.getComponent());
                int textHeight = font.lineHeight;

                // Checks if mouse is within bounds
                if (mouseY >= this.contentMenuHeightStart && mouseY <= this.contentMenuHeightStop &&
                    mouseX >= element.getX() && mouseX <= element.getX() + textWidth &&
                    mouseY >= element.getY() && mouseY <= element.getY() + textHeight) {

                    Style style = element.getComponent().getStyle();
                    ClickEvent click = style.getClickEvent();
                    if (click != null) {
                        this.handleComponentClicked(style);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Mods Panel Scroll
        if (this.modsScrollBar != null && PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 0, this.modButtonsStart, this.modsPanelWidth, this.modButtonsPanelLength)) {
            int maxVal = this.modButtonsLength - this.modButtonsPanelLength;
            int pixelStep = (int) (maxVal * 0.12); // Modify the value by 12%
            pixelStep = Mth.clamp(pixelStep, 5, 30); // Ensures that the step size is within 5-30
            int newValue = (int) (this.modsScrollBar.getValue() - (delta * pixelStep));
            newValue = Mth.clamp(newValue, 0, maxVal);
            this.modsScrollBar.setValue(newValue);
        }
        // Content Panel Scroll
        if (this.contentScrollBar != null && PaCoGuiUtils.isMouseWithin(mouseX, mouseY, this.modsPanelWidth + PADDING_FOUR, this.contentMenuHeightStart, this.contentPanelWidth, this.contentMenuHeight)) {
            int maxVal = this.contentPanelManager.getContentHeight() - this.contentMenuHeight;
            int pixelStep = (int) (maxVal * 0.12); // Modify the value by 12%
            pixelStep = Mth.clamp(pixelStep, 5, 30); // Ensures that the step size is within 5-30
            int newValue = (int) (this.contentScrollBar.getValue() - (delta * pixelStep));
            newValue = Mth.clamp(newValue, 0, maxVal);
            this.contentScrollBar.setValue(newValue);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void renderBlurredBackground(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // TODO: add config to adjust blurriness and fade in time
        // Map Approach
        this.parameters.put("radius", 5.0F);
        // Uniform Holder Approach
        PASS0_MUL.get().set(fadeInProgress);
        PASS1_MUL.get().set(fadeInProgress * 0.5f);
        PASS2_MUL.get().set(fadeInProgress * 0.25f);

        PaCoPostShaders.PACO_BLUR.processPostChain(partialTick, this.parameters);
        minecraft.getMainRenderTarget().bindWrite(false);
    }

    public void setContentScrollBar(@Nullable PaCoSlider contentScrollBar) {
        if (this.contentScrollBar != contentScrollBar) {
            // If there is already a slider we remove it from the widgets
            if (this.contentScrollBar != null)
                this.removeWidget(this.contentScrollBar);
            // We set the content scroll bar field to the new value (which can be null)
            this.contentScrollBar = contentScrollBar;
            // If the new scroll bar isn't null we add it to the widgets
            if (contentScrollBar != null)
                this.addWidget(contentScrollBar);
        }
    }

    /**
     * Adds the given widget to {@link PaCoScreen#panelModButtons} and calls {@link Screen#addWidget(GuiEventListener)}.
     * @param widget The widget that will be added to the Mods Panel.
     */
    private void addModToPanel(AbstractWidget widget) {
        this.panelModButtons.add(widget);
        this.addWidget(widget);
    }

    /**
     * Creates an ordered list containing a {@link ModDataHolder} for each loaded mod.
     * Additionally, it pins a few mods (minecraft, loader, paco) at the top for easy access.
     * @return An ordered list containing {@link ModDataHolder}.
     */
    public ArrayList<ModDataHolder> createOrderedModsList() {
        boolean isForge = Services.PLATFORM.getPlatformName().equals("Forge");
        // We use a map to store "x" mods, (which may load out of order), with a given index
        Map<Integer, ModDataHolder> pinnedMods = new HashMap<>();
        List<ModDataHolder> mods = new ArrayList<>(); // A simple list that will hold all non pinned mods
        // We loop through all the loaded mods and put them in the appropriate "lists".
        for (ModDataHolder holder : PandoraCore.getModHolders()) {
            if (this.filterButton != null && this.filterButton.getFilterType() != ModsFilterButton.FilterType.NONE) {
                // Updates Only
                if (this.filterButton.getFilterType() == ModsFilterButton.FilterType.UPDATES && holder.isOutdated())
                    mods.add(holder);
                // Warnings Only
                if (this.filterButton.getFilterType() == ModsFilterButton.FilterType.WARNINGS && !holder.getModWarnings().isEmpty())
                    mods.add(holder);
                continue;
            }
            // If no filters are present we build the list as we would normally
            switch (holder.getModId()) {
                case "minecraft" -> pinnedMods.put(0, holder);
                case "forge", "fabricloader" -> pinnedMods.put(1, holder);
                case "fabric-api" -> pinnedMods.put(2, holder);
                case "pandora_core" -> pinnedMods.put(isForge ? 2 : 3, holder);
                default -> mods.add(holder);
            }
        }
        // After we populated the "lists", we go through them and return the values in order
        ArrayList<ModDataHolder> finalList = new ArrayList<>();
        pinnedMods.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> finalList.add(entry.getValue()));
        mods.sort(Comparator.comparing(mod -> mod.getModName().toLowerCase()));
        finalList.addAll(mods);
        return finalList;
    }

    /** Called after the filtered mods list has been created, to update the elements in the UI. */
    public void refresh() {
        this.fieldInit(); // Since there is (probably) a new number of mods we need to refresh all the fields
        List<AbstractWidget> newModButtons = new ArrayList<>();
        for (int i = 0; i < this.filteredMods.size(); i++) {
            ModButton modButton = new ModButton(5, this.modButtonsStart + (i * (MOD_BUTTON_HEIGHT + PADDING_ONE)), this.modButtonWidth, MOD_BUTTON_HEIGHT, this.filteredMods.get(i), this);
            if (this.selectedModButton != null && this.selectedModButton.getModDataHolder().getModId().equals(this.filteredMods.get(i).getModId())) {
                modButton.setSelected(true);
                this.selectedModButton = modButton;
            }
            newModButtons.add(modButton);
        }
        // We refresh the "panelModButtons" list, this is used as a "renderable" alternative
        this.panelModButtons.clear();
        this.panelModButtons.addAll(newModButtons);
        // We remove all the ModButtons and the scrollbar from "children" and "narratables"
        // This happens up here, so we can remove the scroll bar before creating the new one#
        this.children.removeIf(element -> element instanceof ModButton || element == this.modsScrollBar);
        this.narratables.removeIf(element -> element instanceof ModButton || element == this.modsScrollBar);
        this.modsScrollBar = null; // We reset the scroll bar to null after we removed it from the lists
        // We create a new scroll bar if one is needed
        if (this.modButtonsLength > this.modButtonsPanelLength) {
            this.modsScrollBar = new PaCoVerticalSlider(this.modsPanelWidth - 7, this.modButtonsStart + PADDING_TWO, 6, this.modButtonsPanelLength + PADDING_ONE, 0, (this.modButtonsLength - this.modButtonsPanelLength), 0, 1)
                    .setSilent(true)
                    .setTextHidden(true)
                    .setHandleSize(8, this.modsHandleHeight)
                    .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                    .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            newModButtons.add(this.modsScrollBar); // We add the scroll bar to the end of the list, as we will require that structure bellow any ways
            // Handles updating the scroll bar on window resizing.
            if (this.selectedModButton != null && this.filteredMods.contains(this.selectedModButton.getModDataHolder()))
                this.selectedModButton.moveButtonIntoFocus(true);
        }
        // We get the index of "filterButton" since it's in the list right before the ModButtons
        // and use it to insert the new ModButtons and scroll bar (if there is one) right after.
        int filterIdx = this.children.indexOf(this.filterButton);
        this.children.addAll(filterIdx + 1, newModButtons);
        filterIdx = this.narratables.indexOf(this.filterButton);
        this.narratables.addAll(filterIdx + 1, newModButtons);
    }
}
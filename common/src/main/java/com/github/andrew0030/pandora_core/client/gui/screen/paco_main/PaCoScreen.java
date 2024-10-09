package com.github.andrew0030.pandora_core.client.gui.screen.paco_main;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.ModsFilterButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModImageManager;
import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.registry.PaCoCoreShaders;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.*;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

public class PaCoScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    public static final Component SEARCH = Component.translatable("gui.pandora_core.paco.search");
    public static final Component NO_MATCHES = Component.translatable("gui.pandora_core.paco.no_matches");
    public static final Component NO_WARNINGS = Component.translatable("gui.pandora_core.paco.no_warnings");
    public static final Component NO_UPDATES = Component.translatable("gui.pandora_core.paco.no_updates");
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


    public PaCoScreen() {
        super(TITLE);
        this.parameters = new HashMap<>();
        this.openTime = System.currentTimeMillis();
    }

    public PaCoScreen(TitleScreen titleScreen) {
        this();
        this.titleScreen = titleScreen;
    }

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
        /* Field Init */
        this.fieldInit();

        /* Adding Widgets */
        String activeSearchText = this.searchBox != null ? this.searchBox.getValue() : null; // If there is text we grab it before the EditBox is discarded.
        this.searchBox = null;
        ModsFilterButton.FilterType filterType = this.filterButton != null ? this.filterButton.getFilterType() : ModsFilterButton.FilterType.NONE;
        this.filterButton = null;
        this.panelModButtons.clear(); // We clear the list (needed because resize would cause duplicates otherwise)
        this.modsScrollBar = null;
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
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        long elapsed = System.currentTimeMillis() - this.openTime;
        int fadeInTime = 160; //TODO add config options for fade in time and blurriness
        this.fadeInProgress = (elapsed + partialTick) < fadeInTime ? (elapsed + partialTick) / fadeInTime : 1.0F;
        // Renders the Panorama if needed
        if (this.titleScreen != null) {
            if (this.titleScreen.fadeInStart == 0L && this.titleScreen.fading)
                this.titleScreen.fadeInStart = Util.getMillis();

            float f = this.titleScreen.fading ? (float)(Util.getMillis() - this.titleScreen.fadeInStart) / 1000.0F : 1.0F;
            this.titleScreen.panorama.render(partialTick, Mth.clamp(f, 0.0F, 1.0F));
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
        if (this.modsScrollBar != null) {
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
        this.filterButton.render(graphics, mouseX, mouseY, partialTick);
        // Renders Mods the Scroll Bar
        if (this.modsScrollBar != null) this.modsScrollBar.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void renderContentPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Panel Background
        RenderSystem.enableBlend();
        graphics.blitRepeating(TEXTURE, this.modsPanelWidth + PADDING_FOUR, this.contentMenuHeightStart, this.contentPanelWidth - PADDING_TWO, this.contentMenuHeight, 0, 122, 48, 48);

        this.contentPanelManager.renderElements(graphics, mouseX, mouseY, partialTick);

        if (this.selectedModButton != null) {
            //TODO maybe move this into a content panel manager element?
//            this.renderModBackground(this.selectedModButton.getModDataHolder(), graphics, this.modsPanelWidth + PADDING_FOUR, this.contentMenuHeightStart, backgroundWidth, backgroundHeight);
            // Debug Outline For Banner
//            PaCoGuiUtils.renderBoxWithRim(graphics, this.modsPanelWidth + PADDING_FOUR, this.contentMenuHeightStart, backgroundWidth, backgroundHeight, null, PaCoColor.color(100, 255, 0, 0), 1);

















            // TODO: the text bellow is placeholder code to get a good feeling for the UI, I will have to add a dynamic system to calculate height more easily
//            graphics.pose().pushPose();
//            graphics.pose().translate(this.modsPanelWidth + PADDING_FOUR + PADDING_FOUR, this.contentMenuHeight / 3.2F, 0F);
//            graphics.pose().scale(2F, 2F, 2F);
//            PaCoGuiUtils.drawWordWrap(graphics, this.font, FormattedText.of(this.selectedModButton.getModDataHolder().getModName()), 0, 0, (this.contentPanelWidth - 8) / 2, PaCoColor.WHITE, true);
//            graphics.pose().popPose();
//
//            graphics.drawString(this.font, "version:", this.modsPanelWidth + PADDING_FOUR + PADDING_FOUR, Mth.ceil(this.contentMenuHeight / 3.2F) + 20, PaCoColor.color(120, 120, 120), true);
//            graphics.drawString(this.font, this.selectedModButton.getModDataHolder().getModVersion(), this.modsPanelWidth + PADDING_FOUR + PADDING_FOUR + PADDING_TWO + this.font.width("version:"), Mth.ceil(this.contentMenuHeight / 3.2F) + 21, PaCoColor.color(220, 220, 220), false);
//
//            if (!this.selectedModButton.getModDataHolder().getModWarnings().isEmpty()) {
//                graphics.drawString(this.font, "warning:", this.modsPanelWidth + PADDING_FOUR + PADDING_FOUR, Mth.ceil(this.contentMenuHeight / 3.2F) + 29, PaCoColor.color(120, 120, 120), true);
//                graphics.drawString(this.font, this.selectedModButton.getModDataHolder().getModWarnings().get(0), this.modsPanelWidth + PADDING_FOUR + PADDING_FOUR + PADDING_TWO + this.font.width("warning:"), Mth.ceil(this.contentMenuHeight / 3.2F) + 30, PaCoColor.color(250, 20, 20), false);
//            }
        }

        // Top Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStart - 4, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
        // Bottom Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStop, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
    }

    @Override
    public void onClose() {
        // Clears the Icon cache
        this.imageManager.close();
        // Handles returning to previous Screen if needed
        if (this.previousScreen != null) {
            Minecraft.getInstance().setScreen(this.previousScreen);
        } else if (this.titleScreen != null) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // If there is a scroll bar and the mouse is over the mods buttons, we move the scroll bar.
        if (this.modsScrollBar != null && PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 0, this.modButtonsStart, this.modsPanelWidth, this.modButtonsPanelLength)) {
            int maxVal = this.modButtonsLength - this.modButtonsPanelLength;
            int pixelStep = (int) (maxVal * 0.12); // Modify the value by 12%
            pixelStep = Mth.clamp(pixelStep, 5, 30); // Ensures that the step size is within 5-30
            int newValue = (int) (this.modsScrollBar.getValue() - (delta * pixelStep));
            newValue = Mth.clamp(newValue, 0, maxVal);
            this.modsScrollBar.setValue(newValue);
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

    public void renderModBackground(ModDataHolder holder, GuiGraphics graphics, int posX, int posY, int width, int height) {
        Pair<ResourceLocation, Pair<Integer, Integer>> backgroundData = this.imageManager.getImageData(
                holder.getModId(),
                this.imageManager::getCachedBackground,
                this.imageManager::cacheBackground,
                holder.getModBackgroundFiles(),
                2F,
                (imgWidth, ingHeight) -> true, //TODO add blurring logic
                "background"
        );
        if (backgroundData != null) {
            // If the ResourceLocation isn't null we render the background.
            ResourceLocation rl = backgroundData.getFirst();
            Pair<Integer, Integer> dimensions = backgroundData.getSecond();

//            graphics.blit(resourceLocation, posX, (posY + height / 2) - (width / 4), width, width / 2, 0, 0, dimensions.getFirst(), dimensions.getSecond(), dimensions.getFirst(), dimensions.getSecond());

            RenderSystem.setShaderTexture(0, rl);
            RenderSystem.setShader(PaCoCoreShaders::getPositionColorTexFullAlphaShader);
            RenderSystem.enableBlend();
            Matrix4f matrix4f = graphics.pose().last().pose();
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            int visible = PaCoColor.color(255, 255, 255, 255);
            int hidden = PaCoColor.color(0, 255, 255, 255);
            float halfBackground = height / 2F; // Visible height from the background center to the background top/bottom
            float imgHeight = width / 2F; // Since the background is 2:1, its height is the width divided by two
            float originTop = (posY + halfBackground) - (imgHeight / 2F); // Original background top position (centered)
            float shift = posY - originTop; // Shift amount: how much we need to move the background  to be within bounds

            // Top Quad with adjustments
            float pY1 = originTop + shift;
            float pY2 = pY1 + (imgHeight / 2F) - shift;
            float v1 = shift / imgHeight;
            float v2 = 0.5F ;
            bufferbuilder.vertex(matrix4f, posX, pY1, 0).color(visible).uv(0F, v1).endVertex(); // Top Left
            bufferbuilder.vertex(matrix4f, posX, pY2, 0).color(visible).uv(0F, v2).endVertex(); // Bottom Left
            bufferbuilder.vertex(matrix4f, posX + width, pY2, 0).color(visible).uv(1F, v2).endVertex(); // Bottom Right
            bufferbuilder.vertex(matrix4f, posX + width, pY1, 0).color(visible).uv(1F, v1).endVertex(); // Top Right

            pY1 = pY2;
            pY2 = posY + height;
            v1 = v2;
            v2 = v1 + (pY2 - pY1) / imgHeight;
            bufferbuilder.vertex(matrix4f, posX, pY1, 0).color(visible).uv(0F, v1).endVertex(); // Top Left
            bufferbuilder.vertex(matrix4f, posX, pY2, 0).color(hidden).uv(0F, v2).endVertex(); // Bottom Left
            bufferbuilder.vertex(matrix4f, posX + width, pY2, 0).color(hidden).uv(1F, v2).endVertex(); // Bottom Right
            bufferbuilder.vertex(matrix4f, posX + width, pY1, 0).color(visible).uv(1F, v1).endVertex(); // Top Right

            BufferUploader.drawWithShader(bufferbuilder.end());
            RenderSystem.disableBlend();
        } else {
            // Otherwise we render a predefined or missing icon texture.
            //TODO render generic backgrounds...
        }
    }
}
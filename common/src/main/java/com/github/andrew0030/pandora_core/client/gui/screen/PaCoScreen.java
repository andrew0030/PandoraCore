package com.github.andrew0030.pandora_core.client.gui.screen;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.ModsFilterButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModIconManager;
import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders.BlurVariables.*;

public class PaCoScreen extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/paco_screen.png");
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    public static final Component SEARCH = Component.translatable("gui.pandora_core.paco.search");
    public final ModIconManager iconManager = new ModIconManager();
    private final Map<String, Object> parameters;
    // Transition Stuff
    private float fadeInProgress = 0.0F;
    private final long openTime;
    // Previous Screen Stuff
    private TitleScreen titleScreen = null;
    private Screen previousScreen = null;
    // Widgets
    private final List<Renderable> modsPanelWidgets = new ArrayList<>();
    private PaCoEditBox searchBox;
    private ModsFilterButton filterButton;
    public PaCoSlider modsScrollBar;
    public ModButton selectedModButton;
    // Misc
    private static final int DARK_GRAY_TEXT_COLOR = PaCoColor.color(130, 130, 130);
    private static final int PADDING_ONE = 1;
    private static final int PADDING_TWO = 2;
    private static final int PADDING_FOUR = 4;
    private static final int MOD_BUTTON_HEIGHT = 25;
//    public final List<ModDataHolder> filteredMods = new ArrayList<>(PandoraCore.getModHolders()); //TODO add filter system...
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

    @Override
    protected void init() {
        /* Field Init */
        this.menuHeight = this.height - 40;
        this.contentMenuHeight = this.height - 20;
        this.menuHeightStart = (this.height - this.menuHeight) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
        this.contentMenuHeightStart = (this.height - this.contentMenuHeight) / 2;
        this.contentMenuHeightStop = this.contentMenuHeightStart + this.contentMenuHeight;
        this.modsPanelWidth = Mth.floor(this.width * 0.32F);
        this.contentPanelWidth = this.width - this.modsPanelWidth - PADDING_TWO;
        this.modButtonsStart = 41;
        this.modButtonsLength = (PandoraCore.getModHolders().size() * (MOD_BUTTON_HEIGHT + PADDING_ONE)) - 1;
        this.modButtonsPanelLength = this.menuHeightStop - this.modButtonsStart;
        this.modButtonWidth = this.modsPanelWidth - (this.modButtonsLength > this.modButtonsPanelLength ? 15 : 10);
        this.modsHandleHeight = Math.max(8, this.modButtonsPanelLength - (this.modButtonsLength - this.modButtonsPanelLength));

        /* Adding Widgets */
        this.searchBox = null;
        this.filterButton = null;
        this.modsPanelWidgets.clear(); // We clear the list (needed because resize would cause duplicates otherwise)
        this.modsScrollBar = null;
        // Search Box
        this.searchBox = new PaCoEditBox(this.font, 7, this.menuHeightStart + 2, this.modsPanelWidth - 27, 14, SEARCH, this);
        this.searchBox.setMaxLength(50);
        this.searchBox.setHint(SEARCH);
        this.searchBox.setTextColor(DARK_GRAY_TEXT_COLOR);
        this.addWidget(this.searchBox);
        // Filter Button
        this.filterButton = new ModsFilterButton(this.modsPanelWidth - 18, this.menuHeightStart);
        this.addWidget(this.filterButton);
        // Mod Buttons
        int idx = 0;
        for (ModDataHolder dataHolder : PandoraCore.getModHolders()) {
            ModButton modButton = new ModButton(5, this.modButtonsStart + (idx * (MOD_BUTTON_HEIGHT + PADDING_ONE)), this.modButtonWidth, MOD_BUTTON_HEIGHT, dataHolder, this);
            if (this.selectedModButton != null && this.selectedModButton.getModDataHolder().getModId().equals(dataHolder.getModId())) {
                modButton.setSelected(true);
                this.selectedModButton = modButton;
            }
            this.addToModsPanel(modButton);
            idx++;
        }
        // Scroll Bar (Slider)
        if (this.modButtonsLength > this.modButtonsPanelLength) { // We only add it if its needed
            this.modsScrollBar = new PaCoVerticalSlider(this.modsPanelWidth - 7, this.modButtonsStart, 6, this.modButtonsPanelLength, 0, (this.modButtonsLength - this.modButtonsPanelLength), 0, 1)
                    .setSilent(true)
                    .setTextHidden(true)
                    .setHandleSize(8, this.modsHandleHeight)
                    .setSliderTexture(TEXTURE, 0, 54, 6, 54, 6, 18, 1)
                    .setHandleTexture(TEXTURE, 12, 54, 20, 54, 8, 18, 1);
            this.addWidget(this.modsScrollBar);
        }

        // Handles updating the scroll bar on window resizing.
        // Needs to be called after a scroll bar was added.
        if (this.selectedModButton != null)
            this.selectedModButton.moveButtonIntoFocus();
    }

    @Override
    public void tick() {
        this.searchBox.tick();

//        System.out.println(this.filteredMods);
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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.2F * this.fadeInProgress);//TODO: look into this darkness fade, once fading has a config
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
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
        // Search Bar
        graphics.blit(TEXTURE, 0, this.menuHeightStart, 1, 36, 5, 18);
        graphics.blit(TEXTURE, 5, this.menuHeightStart, 18, 36, 9, 18);
        graphics.blitRepeating(TEXTURE, 14, this.menuHeightStart, this.modsPanelWidth - 41, 18, 27, 36, 18, 18);
        graphics.blit(TEXTURE, this.modsPanelWidth - 27, this.menuHeightStart, 45, 36, 9, 18);
        // Renders Mod Buttons
        PaCoGuiUtils.enableScissor(graphics, 5, this.modButtonsStart, this.modButtonWidth, this.modButtonsPanelLength);
        graphics.pose().pushPose();
        for (Renderable renderable : this.modsPanelWidgets)
            renderable.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();

        RenderSystem.enableBlend();
        // Mod Buttons Gradients
        if (this.modsScrollBar != null) {
            // Top Gradient
            if (this.modsScrollBar.getValue() > 0) {
                int gradientHeight = (int) Math.min(25, this.modsScrollBar.getValue());
                graphics.blitRepeating(TEXTURE, 5, this.modButtonsStart, this.modButtonWidth, gradientHeight, 25, 122 - gradientHeight, 25, gradientHeight);
            }
            // Bottom Gradient
            int maxVal = this.modButtonsLength - this.modButtonsPanelLength;
            if (this.modsScrollBar.getValue() < maxVal) {
                int gradientHeight = (int) Math.min(25, maxVal - this.modsScrollBar.getValue());
                graphics.blitRepeating(TEXTURE, 5, this.menuHeightStop - gradientHeight, this.modButtonWidth, gradientHeight, 0, 97, 25, gradientHeight);
            }
        }

        // Note: Widgets "should" be rendered after scissors, because certain things like tooltips are weird if rendered before.
        // Renders Search Box and Filter Button
        this.searchBox.render(graphics, mouseX, mouseY, partialTick);
        this.filterButton.render(graphics, mouseX, mouseY, partialTick);
        // Renders Mods the Scroll Bar
        if (this.modsScrollBar != null) this.modsScrollBar.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void renderContentPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        PaCoGuiUtils.renderBoxWithRim(graphics, this.modsPanelWidth + PADDING_FOUR, this.contentMenuHeightStart,  this.contentPanelWidth - PADDING_TWO, this.contentMenuHeight, PaCoColor.color(100, 0, 0, 0), null);

        // Top Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStart - 4, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
        // Bottom Bar
        graphics.blitNineSliced(TEXTURE, this.modsPanelWidth + PADDING_TWO, this.contentMenuHeightStop, this.contentPanelWidth, 4, 1, 17, 18, 0, 36);
    }

    @Override
    public void onClose() {
        // Clears the Icon cache
        this.iconManager.close();
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
     * Adds the given widget to {@link PaCoScreen#modsPanelWidgets} and calls {@link Screen#addWidget(GuiEventListener)}.
     * @param widget The widget that will be added to the Mods Panel.
     */
    private void addToModsPanel(AbstractWidget widget) {
        this.modsPanelWidgets.add(widget);
        this.addWidget(widget);
    }
}
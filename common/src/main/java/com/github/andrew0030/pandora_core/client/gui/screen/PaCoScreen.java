package com.github.andrew0030.pandora_core.client.gui.screen;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.ModsFilterButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModIconManager;
import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoVerticalSlider;
import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.client.utils.gui.enums.PaCoBorderSide;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
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

import static com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry.BlurVariables.*;

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
    // Misc
    private static final int DARK_GRAY_TEXT_COLOR = PaCoColor.color(130, 130, 130);
    private static final int PADDING = 2;
    private int menuHeight;
    private int menuHeightStart;
    private int menuHeightStop;
    private int modsPanelWidth;
    private int contentPanelWidth;

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
        // Field Init
        this.menuHeight = this.height - 40;
        this.menuHeightStart = (this.height - this.menuHeight) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
        this.modsPanelWidth = Mth.floor(this.width * 0.32F);
        this.contentPanelWidth = this.width - this.modsPanelWidth - PADDING;
//          // Adding Widgets
//        int idx = 0;
        this.modsPanelWidgets.clear();
        // Search Box
        this.searchBox = new PaCoEditBox(this.font, 7, this.menuHeightStart + 2, this.modsPanelWidth - 27, 14, SEARCH);
        this.searchBox.setMaxLength(50);
//        this.searchBox.setBordered(false);
        this.searchBox.setHint(SEARCH);
        this.searchBox.setTextColor(DARK_GRAY_TEXT_COLOR);
        this.modsPanelWidgets.add(this.searchBox);
        this.addWidget(this.searchBox);
        // Filter Button
        ModsFilterButton filterButton = new ModsFilterButton(this.modsPanelWidth - 18, this.menuHeightStart, Component.literal(""));//TODO add message
        this.modsPanelWidgets.add(filterButton);
        this.addWidget(filterButton);
//        for (String modId : PandoraCore.getPaCoManagedMods()) {
//            AbstractButton button = new ModButton(2, this.modsButtonsStart + (idx * (modsButtonHeight + 1)), modButtonWidth - 10, modsButtonHeight, Services.PLATFORM.getModDataHolder(modId), this);
//            this.modsPanelButtons.add(button);
//            this.addWidget(button);
//            idx++;
//        }

        PaCoSlider modsSlider = new PaCoVerticalSlider(this.modsPanelWidth - 7, this.menuHeightStart + 21, 6, (this.menuHeightStop - this.menuHeightStart - 21), 0, 100, 0, 1)
                .setSilent(true)
                .setTextHidden(true)
                .setHandleSize(8, 20)
                .setSliderTexture(TEXTURE, 186, 176, 186, 176, 18, 18, 1)
                .setHandleColor(PaCoColor.color(200, 200, 200), PaCoColor.BLACK, PaCoColor.WHITE);
        this.modsPanelWidgets.add(modsSlider);
        this.addWidget(modsSlider);
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
        // Mods Panel Background
        PaCoGuiUtils.renderBoxWithRim(graphics, 0, this.menuHeightStart, this.modsPanelWidth, this.menuHeight, null, PaCoColor.color(255, 40, 40), 1);
        // Search Bar
        graphics.blit(TEXTURE, 0, this.menuHeightStart, 1, 36, 5, 18);
        graphics.blit(TEXTURE, 5, this.menuHeightStart, 18, 36, 9, 18);
        graphics.blitRepeating(TEXTURE, 14, this.menuHeightStart, this.modsPanelWidth - 41, 18, 27, 36, 18, 18);
        graphics.blit(TEXTURE, this.modsPanelWidth - 27, this.menuHeightStart, 45, 36, 9, 18);

        // Renders Mod Buttons
//        graphics.enableScissor(2, this.modsButtonsStart - 2, this.modsPanelWidth - 2, this.menuHeightStop - 1);
        graphics.pose().pushPose();
        for (Renderable renderable : this.modsPanelWidgets)
            renderable.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
//        graphics.disableScissor();
    }

    protected void renderContentPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int rimColor = PaCoColor.color(207, 207, 196);
        PaCoGuiUtils.renderBoxWithRim(graphics, this.modsPanelWidth + 2, this.menuHeightStart,  this.contentPanelWidth, this.menuHeight, PaCoColor.color(100, 0, 0, 0), List.of(
                PaCoBorderSide.TOP.setColor(rimColor).setSize(1),
                PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(1)
        ));
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

    private void renderBlurredBackground(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // TODO: add config to adjust blurriness and fade in time
        // Map Approach
        this.parameters.put("radius", 5.0F);
        // Uniform Holder Approach
        PASS0_MUL.get().set(fadeInProgress);
        PASS1_MUL.get().set(fadeInProgress * 0.5f);
        PASS2_MUL.get().set(fadeInProgress * 0.25f);

        PaCoPostShaderRegistry.PACO_BLUR.processPostChain(partialTick, this.parameters);
        minecraft.getMainRenderTarget().bindWrite(false);
    }
}
package com.github.andrew0030.pandora_core.client.gui.screen;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModButton;
import com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection.ModIconManager;
import com.github.andrew0030.pandora_core.client.utils.gui.enums.PaCoBorderSide;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.client.gui.sliders.HorizontalTextSnap;
import com.github.andrew0030.pandora_core.client.gui.sliders.PaCoSlider;
import com.github.andrew0030.pandora_core.client.gui.sliders.VerticalTextSnap;
import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry.BlurVariables.*;

public class PaCoScreen extends Screen {
    public int menuHeight;
    public int modsPanelWidth;
    public int modContentWidth;
    public int menuHeightStart;
    public int menuHeightStop;
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    public final ModIconManager iconManager = new ModIconManager();
    private final List<Renderable> modsPanelButtons = new ArrayList<>();
    private final Map<String, Object> parameters;
    private float fadeInProgress = 0.0F;
    private final long openTime;
    private TitleScreen titleScreen = null;
    private Screen previousScreen = null;

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
        this.modsPanelWidth = Mth.floor(this.width * 0.32F);
        this.modContentWidth = this.width - this.modsPanelWidth - 2;
        this.menuHeightStart = (this.height - this.menuHeight) / 2;
        this.menuHeightStop = this.menuHeightStart + this.menuHeight;
        // Adding Widgets
        int modsPanelWidth = Mth.floor(this.width * 0.32F);
        int modButtonWidth = modsPanelWidth - 4;
        int modsButtonHeight = 25;
        int idx = 0;
        this.modsPanelButtons.clear();
        for (String modId : PandoraCore.getPaCoManagedMods()) {
            AbstractButton button = new ModButton(2, 23 + (idx * (modsButtonHeight + 1)), modButtonWidth, modsButtonHeight, Services.PLATFORM.getModDataHolder(modId), this);
            this.modsPanelButtons.add(button);
            this.addWidget(button);
            idx++;
        }


        this.addRenderableWidget(new PaCoSlider(this.width / 3, this.height / 2, 300, 12, 0, 60, 0, 1)
                .setSilent(true)
                .setHandleWidth(150)
                .setSliderColor(PaCoColor.color(100, 0, 0, 0), PaCoColor.color(0, 0, 0))
        );
        this.addRenderableWidget(new PaCoSlider(this.width / 3, this.height / 3, 300, 19, 0, 300, 0, 0.5)
                .setPrefix(Component.literal("gval: "))
                .setDropShadow(false)
                .setSilent(true)
                .setTextSnap(HorizontalTextSnap.CENTER, VerticalTextSnap.CENTER)
                .setHandleColor(PaCoColor.color(100, 20, 20), PaCoColor.color(20, 20, 20), PaCoColor.color(255, 255, 255))
                .setHandleSize(5, 25)
                .setTextColor(PaCoColor.color(200, 60, 60))
        );
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

        RenderSystem.disableDepthTest(); // Needed so it works if chat is rendering.
        // TODO: if PaCo menu behaviour is weird, for example elements disappear, maybe look into re-enabling depth test.
        this.renderBlurredBackground(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.2F * fadeInProgress);//TODO: look into this darkness fade, once fading has a config
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


        int slideInTime = 600; //TODO add config options for slide in time
        float slideInProgress = (elapsed + partialTick) < slideInTime ? (elapsed + partialTick) / slideInTime : 1.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideInProgress)), 0.0F, 0.0F);
        // Mods Panel Background
        int rimColor = PaCoColor.color(207, 207, 196);
        PaCoGuiUtils.renderBoxWithRim(graphics, 0, this.menuHeightStart,  this.modsPanelWidth, this.menuHeight, PaCoColor.color(100, 0, 0, 0), List.of(
                PaCoBorderSide.TOP.setColor(rimColor).setSize(1),
                PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(1)
        ));
        // Renders Mod Buttons
        graphics.enableScissor(2, this.menuHeightStart + 1, this.modsPanelWidth - 2, this.menuHeightStop - 1);
        graphics.pose().pushPose();
        for (Renderable renderable : this.modsPanelButtons)
            renderable.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();
        graphics.pose().popPose();

        // Mod Content Panel
        graphics.pose().pushPose();
        graphics.pose().translate(width * (1 - Easing.CUBIC_OUT.apply(slideInProgress)), 0.0F, 0.0F);
        PaCoGuiUtils.renderBoxWithRim(graphics, this.modsPanelWidth + 2, this.menuHeightStart,  this.modContentWidth, this.menuHeight, PaCoColor.color(100, 0, 0, 0), List.of(
                PaCoBorderSide.TOP.setColor(rimColor).setSize(1),
                PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(1)
        ));
        graphics.pose().popPose();

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {}

    @Override
    public boolean isPauseScreen() {
        return false;
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
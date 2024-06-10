package com.github.andrew0030.pandora_core.client.gui.screen;

import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.github.andrew0030.pandora_core.client.shader.holder.PaCoUniformHolder;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class PaCoScreen extends Screen {
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    private final PaCoUniformHolder radius;
    private final PaCoUniformHolder radiusMul;
    private float fadeInProgress = 0.0F;
    private final long openTime;
    private TitleScreen titleScreen = null;
    private Screen previousScreen = null;

    public PaCoScreen() {
        super(TITLE);
        radius = PaCoPostShaderRegistry.PACO_BLUR.getUniform("Radius");
        radiusMul = PaCoPostShaderRegistry.PACO_BLUR.getUniform("RadiusMultiplier");
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        long elapsed = System.currentTimeMillis() - this.openTime;
        int fadeInTime = 160; //TODO add config options for fade in time and blurriness
        this.fadeInProgress = (elapsed + partialTick) < fadeInTime ? (elapsed + partialTick) / fadeInTime : 1.0F;

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

        int slideInTime = 500; //TODO add config options for slide in time
        float slideInProgress = (elapsed + partialTick) < slideInTime ? (elapsed + partialTick) / slideInTime : 1.0F;
        this.renderModsPanel(graphics, 0, 20, Mth.floor(this.width * 0.32F), this.height - 40, FastColor.ARGB32.color(100, 0, 0, 0), slideInProgress);

        // Renders the screens widgets
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {}

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
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
        this.radius.get().set(5);// TODO: add config to adjust blurriness and fade in time
        this.radiusMul.get().set(this.fadeInProgress);
        PaCoPostShaderRegistry.PACO_BLUR.processPostChain(partialTick, null);
        minecraft.getMainRenderTarget().bindWrite(false);
    }

    private void renderModsPanel(GuiGraphics graphics, int posX, int posY, int width, int height, int color, float slideProgress) {
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideProgress)), 0.0F, 0.0F);
        graphics.fill(posX, posY, posX + width, posY + height, color);
//        int rimColor = FastColor.ARGB32.color(255, 207, 207, 196);
//        graphics.fill(posX, posY, posX + width, posY + 1, rimColor);
//        graphics.fill(posX, posY + height - 1, posX + width, posY + height, rimColor);
        graphics.pose().popPose();
    }
}
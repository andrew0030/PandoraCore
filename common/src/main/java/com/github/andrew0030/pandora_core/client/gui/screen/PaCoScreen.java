package com.github.andrew0030.pandora_core.client.gui.screen;

import com.github.andrew0030.pandora_core.client.shader.PaCoPostShaderRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

import java.util.HashMap;
import java.util.Map;

public class PaCoScreen extends Screen {
    public static final Component TITLE = Component.translatable("gui.pandora_core.paco.title");
    private final Map<String, Object> parameters;
    private float fadeInProgress = 0.0F;
    private final long openTime;

    public PaCoScreen() {
        super(TITLE);
        parameters = new HashMap<>();
        this.openTime = System.currentTimeMillis();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        long elapsed = System.currentTimeMillis() - openTime;
        int fadeInTime = 150; //TODO add config options for fade in time and blurriness
        this.fadeInProgress = elapsed < fadeInTime ? (float) elapsed / fadeInTime : 1.0F;

        this.renderBlurredBackground(partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.2F * fadeInProgress);//TODO: look into this darkness fade, once fading has a config
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.fill(0, 20, this.width, this.height - 20, FastColor.ARGB32.color(100, 0, 0, 0));

        // Renders the screens widgets
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {}

    private void renderBlurredBackground(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        parameters.put("multiplier", this.fadeInProgress);
        PaCoPostShaderRegistry.PACO_BLUR.processPostChain(partialTick, parameters);
        minecraft.getMainRenderTarget().bindWrite(false);
    }
}
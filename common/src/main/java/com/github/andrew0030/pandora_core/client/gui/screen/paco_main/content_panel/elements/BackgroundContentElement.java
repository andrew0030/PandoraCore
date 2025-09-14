package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.registry.PaCoCoreShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackgroundContentElement extends BaseContentElement {
    public static final List<ResourceLocation> MOD_MISSING_BACKGROUNDS = new ArrayList<>();
    public static final HashMap<String, ResourceLocation> MOD_BACKGROUNDS = new HashMap<>();

    static {
        int totalMissingBackgrounds = 9;
        for (int i = 0; i < totalMissingBackgrounds; i++) {
            MOD_MISSING_BACKGROUNDS.add(new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/backgrounds/missing/missing_" + i + ".png"));
        }
        MOD_BACKGROUNDS.put("minecraft", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/backgrounds/mc_background.png"));
        MOD_BACKGROUNDS.put("forge", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/backgrounds/forge_background.png"));
        MOD_BACKGROUNDS.put("fabricloader", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/backgrounds/fabric_background.png"));
        MOD_BACKGROUNDS.put("fabric-api", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/backgrounds/fabric_background.png"));
    }

    public BackgroundContentElement(PaCoContentPanelManager manager) {
        this(manager, 0, 0);
    }

    public BackgroundContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        super(manager, offsetX, offsetY);
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PaCoScreen screen = this.manager.getScreen();
        this.renderModBackground(screen.selectedModButton.getModDataHolder(), graphics, this.getX(), this.getY(), this.manager.getWidth(), this.getHeight());

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE)
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 40, 40), 1);
    }

    @Override
    public int getHeight() {
        return this.manager.getScreen().contentMenuHeight / 3;
    }

    public void renderModBackground(ModDataHolder holder, GuiGraphics graphics, int posX, int posY, int width, int height) {
        Pair<ResourceLocation, Pair<Integer, Integer>> backgroundData = this.manager.getScreen().imageManager.getImageData(
                holder.getModId(),
                this.manager.getScreen().imageManager::getCachedBackground,
                this.manager.getScreen().imageManager::cacheBackground,
                holder.getModBackgroundFiles(),
                2F,
                (imgWidth, ingHeight) -> true, //TODO add blurring logic
                "background"
        );

        if (width < height * 2) {
            posX = (posX + (width / 2)) - (height); // Centers the banner
            width = height * 2; // Ensures the banner maintains a 2:1 ratio
        }

        // If the ResourceLocation isn't null we render the background.
        ResourceLocation rl;
        if (backgroundData != null) {
            rl = backgroundData.getFirst();
        } else {
            rl = MOD_BACKGROUNDS.getOrDefault(holder.getModId(), MOD_MISSING_BACKGROUNDS.get(Math.abs(holder.getModId().hashCode()) % MOD_MISSING_BACKGROUNDS.size()));
        }

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
    }
}
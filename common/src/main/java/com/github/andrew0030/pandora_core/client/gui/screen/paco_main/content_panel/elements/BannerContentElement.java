package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

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

// TODO: Add a HashMap of String as a key and Tripple (Pair alternative I need to make) that will contain a texture path and desired width/height to render if no paco banner was specified, with a higher priority than "forge logos".
// TODO: Finish implementing banner loading on Fabric by adding the getter method to the FabricDataHolder class
public class BannerContentElement extends BaseContentElement {

    public BannerContentElement(PaCoContentPanelManager manager) {
        this(manager, 0, 0);
    }

    public BannerContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        super(manager, offsetX, offsetY);
        this.initializeHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PaCoScreen screen = this.manager.getScreen();
        this.renderModBanner(screen.selectedModButton.getModDataHolder(), graphics, this.getX(), this.getY(), this.manager.getWidth(), this.getHeight());

        // Debug Outline
        // Moved down into this#renderModBanner to have the inner outline render after the outer one
    }

    @Override
    public int getHeight() {
        return (this.manager.getScreen().contentMenuHeight / 3) - 16;
    }

    public void renderModBanner(ModDataHolder holder, GuiGraphics graphics, int posX, int posY, int width, int height) {
        Pair<ResourceLocation, Pair<Integer, Integer>> bannerData = this.manager.getScreen().imageManager.getImageData(
                holder.getModId(),
                this.manager.getScreen().imageManager::getCachedBanner,
                this.manager.getScreen().imageManager::cacheBanner,
                holder.getModBannerFiles(),
                0.5F,
                8F,//TODO maybe tweak or disable aspect ratio
                (imgWidth, ingHeight) -> false, //TODO add blurring logic
                "banner"
        );
        if (bannerData == null) return;

        // If the ResourceLocation isn't null we render the banner.
        ResourceLocation rl = bannerData.getFirst();
        RenderSystem.setShaderTexture(0, rl);
        RenderSystem.setShader(PaCoCoreShaders::getPositionColorTexFullAlphaShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        int imageWidth = bannerData.getSecond().getFirst();
        int imageHeight = bannerData.getSecond().getSecond();
        int paddingHorizontal = 8;
        int paddingVertical = 4;
        float containerX = posX + paddingHorizontal;
        float containerY = posY + paddingVertical;
        float containerW = width - (2 * paddingHorizontal);
        float containerH = height - (2 * paddingVertical);

        // just in case
        if (imageWidth <= 0 || imageHeight <= 0 || containerW <= 0 || containerH <= 0) {
            RenderSystem.disableBlend();
            return;
        }

        // Scale based on whether the banner is touching the container horizontally or vertically
        float scale = Math.min(containerW / (float) imageWidth, containerH / (float) imageHeight);
        float drawWidth = imageWidth * scale;
        float drawHeight = imageHeight * scale;

        // Centers the banner inside the "container"
        float drawX = containerX + (containerW - drawWidth) * 0.5f;
        float drawY = containerY + (containerH - drawHeight) * 0.5f;

        // Rounds the values to avoid subpixel blurring
        drawX = Math.round(drawX);
        drawY = Math.round(drawY);
        drawWidth = Math.round(drawWidth);
        drawHeight = Math.round(drawHeight);

        // The banner construction
        bufferbuilder.vertex(matrix4f, drawX, drawY, 0).color(PaCoColor.WHITE).uv(0F, 0F).endVertex(); // Top Left
        bufferbuilder.vertex(matrix4f, drawX, drawY + drawHeight, 0).color(PaCoColor.WHITE).uv(0F, 1F).endVertex(); // Bottom Left
        bufferbuilder.vertex(matrix4f, drawX + drawWidth, drawY + drawHeight, 0).color(PaCoColor.WHITE).uv(1F, 1F).endVertex(); // Bottom Right
        bufferbuilder.vertex(matrix4f, drawX + drawWidth, drawY, 0).color(PaCoColor.WHITE).uv(1F, 0F).endVertex(); // Top Right

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE) {
            // Content Element Outline
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 128, 128), 1);
            // Banner Outline
            PaCoGuiUtils.renderBoxWithRim(graphics, (int) drawX, (int) drawY, (int) drawWidth, (int) drawHeight, null, PaCoColor.color(130, 0, 255), 1);
        }
    }
}
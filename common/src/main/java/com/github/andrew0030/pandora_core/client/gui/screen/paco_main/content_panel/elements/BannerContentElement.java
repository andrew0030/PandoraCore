package com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.elements;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.registry.PaCoCoreShaders;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.tuple.Triple;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Optional;

public class BannerContentElement extends BaseContentElement {
    public static final HashMap<String, Pair<String, String>> MOD_BANNERS = new HashMap<>();

    static {
        MOD_BANNERS.put("minecraft",    sameNamespacePair("minecraft", "textures/gui/title/minecraft.png"));
        MOD_BANNERS.put("pandora_core", sameNamespacePair("minecraft", "textures/gui/title/minecraft.png"));
    }

    public BannerContentElement(PaCoContentPanelManager manager) {
        this(manager, 0, 0);
    }

    public BannerContentElement(PaCoContentPanelManager manager, int offsetX, int offsetY) {
        super(manager, offsetX, offsetY);
        this.initializeHeight();
    }

    @Deprecated(forRemoval = false)
    @ApiStatus.Internal
    public static Optional<Pair<String, String>> getInternalFallbackResourceLocation(String modId) {
        return Optional.ofNullable(MOD_BANNERS.get(modId));
    }

    /**
     * A convenience method to allow loading images like a {@link ResourceLocation}.<br/>
     * Creates a {@link Pair} containing a Mod-ID {@code namespace} and a <b>full</b> {@code path} using the same {@code namespace}.<br/>
     * Meaning that the second {@link String} will then be {@code assets/<namespace>/<path>}.
     * @param namespace The Mod-ID of the Mod containing the texture
     * @param path      The path to the texture. (Needs to be inside {@code assets/<namespace>/}
     */
    private static Pair<String, String> sameNamespacePair(String namespace, String path) {
        return Pair.of(namespace, String.format("assets/%s/%s", namespace, path));
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

        Triple<ResourceLocation, Integer, Integer> bannerData = this.manager.getScreen().imageManager.getImageData(
                holder.getModId(),
                this.manager.getScreen().imageManager::getCachedBanner,
                this.manager.getScreen().imageManager::cacheBanner,
                holder.getModBannerFiles(),
                0.5F,
                8F,//TODO maybe tweak or disable aspect ratio
                (imgWidth, ingHeight) -> true, //TODO add blurring logic
                (imgWidth, ingHeight) -> true,
                "banner"
        );

        // If no valid banner was found, there is nothing to render
        if (bannerData == null) return;

        ResourceLocation rl = bannerData.getFirst();;
        int imageWidth = bannerData.getSecond();
        int imageHeight = bannerData.getThird();

        int paddingHorizontal = 8;
        int paddingVertical = 4;
        float containerX = posX + paddingHorizontal;
        float containerY = posY + paddingVertical;
        float containerW = width - (2 * paddingHorizontal);
        float containerH = height - (2 * paddingVertical);

        // Just in case, if any of the values are 0 we return early
        if (imageWidth <= 0 || imageHeight <= 0 || containerW <= 0 || containerH <= 0) return;

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

        RenderSystem.setShaderTexture(0, rl);
        RenderSystem.setShader(PaCoCoreShaders::getPositionColorTexFullAlphaShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        // The banner quad construction
        bufferbuilder.vertex(matrix4f, drawX,             drawY,              0).color(PaCoColor.WHITE).uv(0F, 0F).endVertex(); // Top Left
        bufferbuilder.vertex(matrix4f, drawX,             drawY + drawHeight, 0).color(PaCoColor.WHITE).uv(0F, 1F).endVertex(); // Bottom Left
        bufferbuilder.vertex(matrix4f, drawX + drawWidth, drawY + drawHeight, 0).color(PaCoColor.WHITE).uv(1F, 1F).endVertex(); // Bottom Right
        bufferbuilder.vertex(matrix4f, drawX + drawWidth, drawY,              0).color(PaCoColor.WHITE).uv(1F, 0F).endVertex(); // Top Right
        // Renders the constructed quad
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
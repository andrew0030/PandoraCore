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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.HashMap;

// TODO: Finish implementing banner loading on Fabric by adding the getter method to the FabricDataHolder class
public class BannerContentElement extends BaseContentElement {
    public static final HashMap<String, Triple<ResourceLocation, Integer, Integer>> MOD_BANNERS = new HashMap<>();

    static {
        MOD_BANNERS.put("minecraft", Triple.of(new ResourceLocation("textures/gui/title/minecraft.png"), 1024, 256));
//        MOD_BANNERS.put("minecraft", Triple.of(new ResourceLocation(PandoraCore.MOD_ID, "textures/test.png"), 3, 2));
    }

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
        Triple<ResourceLocation, Integer, Integer> bannerData = this.manager.getScreen().imageManager.getImageData(
                holder.getModId(),
                this.manager.getScreen().imageManager::getCachedBanner,
                this.manager.getScreen().imageManager::cacheBanner,
                holder.getModBannerFiles(),
                0.5F,
                8F,//TODO maybe tweak or disable aspect ratio
                (imgWidth, ingHeight) -> true, //TODO add blurring logic
                "banner"
        );

        ResourceLocation rl;
        int imageWidth = 0;
        int imageHeight = 0;
        if (bannerData != null) {
            // If a valid banner was provided we render that
            rl = bannerData.getFirst();
            imageWidth = bannerData.getSecond();
            imageHeight = bannerData.getThird();
        } else if (MOD_BANNERS.containsKey(holder.getModId())) {
            // If no valid banner was provided but the entry was in MOD_BANNERS we render that
            Triple<ResourceLocation, Integer, Integer> triple = MOD_BANNERS.get(holder.getModId());
            rl = triple.getFirst();
            imageWidth = triple.getSecond();
            imageHeight = triple.getThird();
        } else {
            // Lastly if no valid banner was found, there is nothing to render
            return;
        }

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

        // Stores the previous texture wrap mode so we can reset it after we are done rendering the quad
        int prevWrapModeS = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S);
        int prevWrapModeT = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T);

        RenderSystem.setShaderTexture(0, rl);
        /*
         * Sets the texture wrap mode to "GL_CLAMP_TO_EDGE" to avoid scaling artifacts.
         *
         * NOTE: Should this ever break due to a Mod that replaces Blaze3D or adds Vulkan or something crazy,
         * I have left commented out code inside the "position_color_tex_full_alpha" fragment shader. Which can
         * be used to clamp the "Sampler" and make it sample with half-texel offset, effectively simulating what
         * this raw GL11 code does. At the cost of worse performance.
         */
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
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
        // Resets the texture wrap mode to what ever it was before we rendered our banner
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, prevWrapModeS);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, prevWrapModeT);

        // Debug Outline
        if (PaCoContentPanelManager.DEBUG_MODE) {
            // Content Element Outline
            PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null, PaCoColor.color(255, 128, 128), 1);
            // Banner Outline
            PaCoGuiUtils.renderBoxWithRim(graphics, (int) drawX, (int) drawY, (int) drawWidth, (int) drawHeight, null, PaCoColor.color(130, 0, 255), 1);
        }
    }
}
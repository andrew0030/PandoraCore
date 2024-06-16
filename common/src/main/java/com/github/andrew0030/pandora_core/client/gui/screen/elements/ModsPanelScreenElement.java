package com.github.andrew0030.pandora_core.client.gui.screen.elements;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoBorderSide;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ModsPanelScreenElement {
    public static final ResourceLocation MISSING_LOGO = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/missing_logo.png");
    private final ModLogoManager logoManager;

    public ModsPanelScreenElement(ModLogoManager logoManager) {
        this.logoManager = logoManager;
    }

    public void render(GuiGraphics graphics, int posX, int posY, int width, int height, int color, float slideProgress) {
        graphics.pose().pushPose();
        graphics.pose().translate(-width * (1 - Easing.CUBIC_OUT.apply(slideProgress)), 0.0F, 0.0F);

        int rimColor = PaCoColor.color(255, 207, 207, 196);
//        int rimColor = PaCoColor.colorFromHSV(((PaCoClientTicker.getGame() * 8) + PaCoClientTicker.getPartialTick()) % 360, 1F, 1F);
        PaCoGuiUtils.renderBoxWithRim(graphics, posX, posY, width, height, color, List.of(
                PaCoBorderSide.TOP.setColor(rimColor).setSize(1),
                PaCoBorderSide.BOTTOM.setColor(rimColor).setSize(1)
        ));

        int idx = 0;
        for (String modId : PandoraCore.getPaCoManagedMods()) {
            // Mod Panel Background
            PaCoGuiUtils.renderBoxWithRim(graphics, posX + 2, posY + 2 + (22 * idx), width - 4, 21, PaCoColor.color(100, 0, 0, 0), null);

            // Mod Icon
            this.renderModIcon(modId, graphics, posX + 3, posY + 3 + (22 * idx), 19);

            // Mod ID
            graphics.drawString(Minecraft.getInstance().font, modId, posX + 24, posY + 4 + (22 * idx), PaCoColor.color(255, 255, 255), false);
            graphics.drawString(Minecraft.getInstance().font, "v1.0.0", posX + 24, posY + 14 + (22 * idx), PaCoColor.color(100, 100, 100), false);
            idx++;
        }

        graphics.pose().popPose();
    }

    private void renderModIcon(String modId, GuiGraphics graphics, int posX, int posY, int size) {
        // Attempts to load the logo of a Mod with the given mod ID.
        // If this attempt succeeds logoData is used to get the ResourceLocation and dimensions of the logo.
        // If the attempt fails the placeholder image is rendered.
        Pair<ResourceLocation, Pair<Integer, Integer>> logoData = Services.PLATFORM.getModLogoFile(modId).map(logoFile -> {
            // Checks if the modId is already present in the cache, and grabs the values if so.
            if (this.logoManager.isPresent(modId)) {
                NativeImage cachedImage = this.logoManager.getCachedTexture(modId).getPixels();
                return Pair.of(this.logoManager.getCachedLocation(modId), Pair.of(cachedImage.getWidth(), cachedImage.getHeight()));
            }
            // If the cache doesn't contain the mod we create the logo.
            final Pair<ResourceLocation, Pair<Integer, Integer>>[] result = new Pair[1];
            result[0] = Pair.of(null, Pair.of(0, 0)); // Default value in case of failure

            Services.PLATFORM.loadNativeImage(modId, logoFile, nativeImage -> {
                try {
                    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage) {
                        @Override
                        public void upload() {
                            this.bind();
                            NativeImage image = this.getPixels();
                            this.getPixels().upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), true, false, false, false);
                        }
                    };
                    // We register the texture
                    ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register("modlogo", dynamicTexture);
                    // We cache the loaded texture for easy access next time.
                    this.logoManager.cacheModLogo(modId, resourceLocation, dynamicTexture);
                    // Sets the result, using the new texture and its dimensions.
                    result[0] = Pair.of(resourceLocation, Pair.of(nativeImage.getWidth(), nativeImage.getHeight()));
                } catch (Exception ignored) {}
            });
            return result[0];
        }).orElse(Pair.of(null, Pair.of(0, 0)));

        if (logoData.getFirst() != null) {
            // If the ResourceLocation isn't null we render the logo.
            ResourceLocation resourceLocation = logoData.getFirst();
            Pair<Integer, Integer> dimensions = logoData.getSecond();
            graphics.blit(resourceLocation, posX, posY, size, size, 0, 0, dimensions.getFirst(), dimensions.getSecond(), dimensions.getFirst(), dimensions.getSecond());
        } else {
            // Otherwise we render the missing logo texture.
            graphics.blit(MISSING_LOGO, posX, posY, size, size, 0, 0, 64, 64, 64, 64);
        }
    }
}
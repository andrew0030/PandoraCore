package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoBorderSide;
import com.github.andrew0030.pandora_core.client.gui.screen.utils.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModButton extends AbstractButton {
    public static final ResourceLocation MISSING_MOD_ICON = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/missing_mod_icon.png");
    private final ModDataHolder modDataHolder;
    private final ModIconManager iconManager;

    public ModButton(int x, int y, int width, int height, ModDataHolder modDataHolder, ModIconManager iconManager) {
        super(x, y, width, height, Component.literal(modDataHolder.getModNameOrId()));
        this.modDataHolder = modDataHolder;
        this.iconManager = iconManager;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Button Background
        int boxColor = PaCoColor.color(100, 0, 0, 0);
        ArrayList<PaCoBorderSide> buttonRims = null;
        if (this.isHovered()) {
            boxColor = PaCoColor.color(100, 90, 90, 90);
            buttonRims = PaCoGuiUtils.getBorderList();
            buttonRims.add(PaCoBorderSide.TOP.setSize(1).setColor(PaCoColor.color(157, 157, 146)));
            buttonRims.add(PaCoBorderSide.RIGHT.setSize(1).setColor(PaCoColor.color(157, 157, 146)));
            buttonRims.add(PaCoBorderSide.BOTTOM.setSize(1).setColor(PaCoColor.color(157, 157, 146)));
            buttonRims.add(PaCoBorderSide.LEFT.setSize(1).setColor(PaCoColor.color(157, 157, 146)));
        }
        // Mod Button Background
        PaCoGuiUtils.renderBoxWithRim(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), boxColor, buttonRims);
        // Mod Icon
        this.renderModIcon(this.modDataHolder.getModId(), graphics, this.getX() + 1, this.getY() + 1, this.getHeight() - 2);
        // Mod Name
        graphics.drawString(Minecraft.getInstance().font, this.modDataHolder.getModNameOrId(), this.getX() + this.getHeight() + 2, this.getY() + 3, PaCoColor.color(255, 255, 255), false);
        // Mod Version
        graphics.drawString(Minecraft.getInstance().font, (modDataHolder.getModVersion() == null ? "unknown" : "v" + modDataHolder.getModVersion()), this.getX() + this.getHeight() + 2, this.getY() + 14, PaCoColor.color(130, 130, 130), false);
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    private void renderModIcon(String modId, GuiGraphics graphics, int posX, int posY, int size) {
        Pair<ResourceLocation, Pair<Integer, Integer>> iconData = this.getIconData(modId);
        if (iconData != null) {
            // If the ResourceLocation isn't null we render the icon.
            ResourceLocation resourceLocation = iconData.getFirst();
            Pair<Integer, Integer> dimensions = iconData.getSecond();
            graphics.blit(resourceLocation, posX, posY, size, size, 0, 0, dimensions.getFirst(), dimensions.getSecond(), dimensions.getFirst(), dimensions.getSecond());
        } else {
            // Otherwise we render the missing icon texture.
            graphics.blit(MISSING_MOD_ICON, posX, posY, size, size, 0, 0, 64, 64, 64, 64);
        }
    }

    private Pair<ResourceLocation, Pair<Integer, Integer>> getIconData(String modId) {
        // Attempts to load the icon of a Mod with the given mod ID.
        // If this attempt succeeds iconData is used to get the ResourceLocation and dimensions of the icon.
        // If the attempt fails the placeholder image is rendered.
        String iconFile = this.modDataHolder.getModIconFile();
        if (iconFile != null) {
            // Checks if the modId is already present in the cache, and grabs the values if so.
            if (this.iconManager.isPresent(modId)) {
                NativeImage cachedImage = this.iconManager.getCachedTexture(modId).getPixels();
                return Pair.of(this.iconManager.getCachedLocation(modId), Pair.of(cachedImage.getWidth(), cachedImage.getHeight()));
            }
            // If the cache doesn't contain the mod we create the icon.
            final Pair<ResourceLocation, Pair<Integer, Integer>>[] result = new Pair[1];
            result[0] = Pair.of(null, Pair.of(0, 0)); // Default value in case of failure

            Services.PLATFORM.loadNativeImage(modId, iconFile, nativeImage -> {
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
                    ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register("modicon", dynamicTexture);
                    // We cache the loaded texture for easy access next time.
                    this.iconManager.cacheModIcon(modId, resourceLocation, dynamicTexture);
                    // Sets the result, using the new texture and its dimensions.
                    result[0] = Pair.of(resourceLocation, Pair.of(nativeImage.getWidth(), nativeImage.getHeight()));
                } catch (Exception ignored) {}
            });
            return result[0];
        }
        return null;
    }
}
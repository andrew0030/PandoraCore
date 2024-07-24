package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModButton extends AbstractButton {
    public static final ResourceLocation MISSING_MOD_ICON = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/missing_mod_icon.png");
    private final ModDataHolder modDataHolder;
    private final PaCoScreen screen;

    public ModButton(int x, int y, int width, int height, ModDataHolder modDataHolder, PaCoScreen screen) {
        super(x, y, width, height, Component.literal(modDataHolder.getModNameOrId()));
        this.modDataHolder = modDataHolder;
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

//        boolean isTopInBounds = PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 5, this.screen.modButtonsStart, this.screen.modButtonWidth, this.screen.modButtonsPanelLength);

        boolean inBounds = PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 5, this.screen.modButtonsStart, this.screen.modButtonWidth, this.screen.modButtonsPanelLength);
        this.isHovered = inBounds && this.isHovered;
//        this.active = inBounds;

        RenderSystem.enableBlend();
        // Mod Button
        boolean highlight = this.isHoveredOrFocused();
        graphics.blit(PaCoScreen.TEXTURE, this.getX(), this.getY(), highlight ? 75 : 0, 72, 25, 25); // Mod Icon Background Blit
        graphics.blitRepeating(PaCoScreen.TEXTURE, this.getX() + 25, this.getY(), this.getWidth() - 50, 25, highlight ? 100 : 25, 72, 25, 25); // Button Center
        graphics.blit(PaCoScreen.TEXTURE, this.getX() + this.getWidth() - 25, this.getY(), highlight ? 125 : 50, 72, 25, 25); // Button Right Side End
        // Mod Icon
        this.renderModIcon(this.modDataHolder.getModId(), graphics, this.getX() + 1, this.getY() + 1, this.getHeight() - 2);
        // Mod Name
        graphics.drawString(Minecraft.getInstance().font, this.modDataHolder.getModNameOrId(), this.getX() + this.getHeight() + 2, this.getY() + 3, PaCoColor.color(255, 255, 255), false);
        // Mod Version
        graphics.drawString(Minecraft.getInstance().font, (modDataHolder.getModVersion() == null ? "unknown" : "v" + modDataHolder.getModVersion()), this.getX() + this.getHeight() + 2, this.getY() + 14, PaCoColor.color(130, 130, 130), false);
    }

    @Override
    public int getY() {
        int y = super.getY();
        if (this.screen.modsScrollBar != null)
            return (int) (y - this.screen.modsScrollBar.getValue());
        return y;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            // Moves Button into bounds if it's partially cut of.
            int padding = 16; // We use padding so because the gradient would interfere with the buttons otherwise.
            if (this.getY() < this.screen.modButtonsStart + padding) { // Top Area
                int pixels = this.screen.modButtonsStart - this.getY();
                this.screen.modsScrollBar.setValue(this.screen.modsScrollBar.getValue() - (pixels + padding));
            } else if (this.getY() + this.getHeight() > this.screen.menuHeightStop - padding) { // Bottom Area
                int pixels = this.getY() + this.getHeight() - this.screen.menuHeightStop;
                this.screen.modsScrollBar.setValue(this.screen.modsScrollBar.getValue() + (pixels + padding));
            }
        }
    }

    @Override
    public void onPress() {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 5, this.screen.modButtonsStart, this.screen.modButtonWidth, this.screen.modButtonsPanelLength)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
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

    /**
     * Attempts to load the icon of a Mod with the given mod ID.
     * @param modId the id of the mod to load the icon for
     * @return If this attempt succeeds iconData is used to get the ResourceLocation and dimensions of the icon.
     *         If the attempt fails, it returns null and the placeholder image should be rendered.
     */
    @Nullable
    private Pair<ResourceLocation, Pair<Integer, Integer>> getIconData(String modId) {
        String iconFile = this.modDataHolder.getModIconFile();
        // If the mod doesn't have an icon file, then there's no point in continuing.
        if (iconFile == null)
            return null;

        // Check if the modId is already present in the cache, and grabs the values if so.
        Pair<ResourceLocation, DynamicTexture> cachedEntry = this.screen.iconManager.getCachedEntry(modId);
        if (cachedEntry != null) {
            // If the cached entry's resource location is null, then null should be returned for consistency reasons
            // The first entry being null indicates that the mod has an icon, but said icon failed to load
            if (cachedEntry.getFirst() == null)
                return null;

            // return the entry
            return Pair.of(
                    cachedEntry.getFirst(),
                    Pair.of(cachedEntry.getSecond().getPixels().getWidth(), cachedEntry.getSecond().getPixels().getHeight())
            );
        }

        // If the icon is not already cached, then load it
        return Services.PLATFORM.loadNativeImage(modId, iconFile, nativeImage -> {
            if (nativeImage == null) {
                // If the icon fails to load, null is provided
                // So cache null,null to indicate that
                this.screen.iconManager.cacheModIcon(modId, null, null);
                return null;
            }

            DynamicTexture dynamicTexture = null;
            try {
                dynamicTexture = new DynamicTexture(nativeImage) {
                    @Override
                    public void upload() {
                        this.bind();
                        NativeImage image = this.getPixels();
                        this.getPixels().upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), true, false, false, false);
                    }
                };
                // Register and cache the texture, and return it
                ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register("modicon", dynamicTexture);
                this.screen.iconManager.cacheModIcon(modId, resourceLocation, dynamicTexture);
                return Pair.of(resourceLocation, Pair.of(nativeImage.getWidth(), nativeImage.getHeight()));
            } catch (Exception ignored) {
                // Safety reasons
                // To my knowledge, the try here should never fail
                // However, VRAM leaks are particularly annoying in that they're unnoticeable until the device crashes, at which point the screen blacks out until drivers reboot
                if (dynamicTexture != null)
                    dynamicTexture.close();

                // Cache null,null so that the icon doesn't load again
                this.screen.iconManager.cacheModIcon(modId, null, null);
                return null;
            }
        });
    }
}
package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ModButton extends AbstractButton {
    public static final ResourceLocation MISSING_MOD_ICON = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/missing_mod_icon.png");
    public static final HashMap<String, ResourceLocation> MOD_ICONS = new HashMap<>();
    private final ModDataHolder modDataHolder;
    private final PaCoScreen screen;
    private boolean selected;
    private int versionColor = PaCoColor.color(130, 130, 130);

    static {
        MOD_ICONS.put("minecraft", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/mc_mod_icon.png"));
        MOD_ICONS.put("forge", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/forge_mod_icon.png"));
    }

    public ModButton(int x, int y, int width, int height, ModDataHolder modDataHolder, PaCoScreen screen) {
        super(x, y, width, height, Component.literal(modDataHolder.getModName()));
        this.modDataHolder = modDataHolder;
        this.screen = screen;
        modDataHolder.getUpdateStatus().ifPresent(status -> this.versionColor = status.isOutdated() ? PaCoColor.color(200, 150, 10) : this.versionColor);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean mouseInBounds = PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 5, this.screen.modButtonsStart, this.screen.modButtonWidth, this.screen.modButtonsPanelLength);
        this.isHovered = mouseInBounds && this.isHovered;
        // If buttons are fully outside the bounds of the panel, we "cull" them
        if (this.getY() + this.getHeight() > this.screen.modButtonsStart && this.getY() < this.screen.modButtonsStart + this.screen.modButtonsPanelLength) {
            RenderSystem.enableBlend();
            // Mod Button
            int offsetU = 0;
            if (this.isHoveredOrFocused())
                offsetU = 75;
            if (this.isSelected())
                offsetU = 150;
            // Background
            graphics.blit(PaCoScreen.TEXTURE, this.getX(), this.getY(), offsetU, 72, 25, 25); // Mod Icon Background Blit
            graphics.blitRepeating(PaCoScreen.TEXTURE, this.getX() + 25, this.getY(), this.getWidth() - 50, 25, 25 + offsetU, 72, 25, 25); // Button Center
            graphics.blit(PaCoScreen.TEXTURE, Math.max((this.getX() + this.getWidth() - 25), (this.getX() + 25)), this.getY(), 50 + offsetU, 72, 25, 25); // Button Right Side End
            // Mod Icon
            this.renderModIcon(this.modDataHolder, graphics, this.getX() + 1, this.getY() + 1, this.getHeight() - 2);
            // Mod Name
            String name = this.modDataHolder.getModName();
            int availableWidth = this.getWidth() - 27; // Total width minus icon width and padding.
            int nameWidth = Minecraft.getInstance().font.width(name);
            if (nameWidth > availableWidth) {
                name = Minecraft.getInstance().font.plainSubstrByWidth(name, availableWidth - Minecraft.getInstance().font.width("...")).trim().concat("...");
            }
            graphics.drawString(Minecraft.getInstance().font, name, this.getX() + this.getHeight() + 2, this.getY() + 3, PaCoColor.color(255, 255, 255), false);
            // Mod Version
            String version = "v" + this.modDataHolder.getModVersion();
            int versionWidth = Minecraft.getInstance().font.width(version);
            if (versionWidth > availableWidth) {
                version = Minecraft.getInstance().font.plainSubstrByWidth(version, availableWidth - Minecraft.getInstance().font.width("...")).concat("...");
            }
            graphics.drawString(Minecraft.getInstance().font, version, this.getX() + this.getHeight() + 2, this.getY() + 14, this.versionColor, false);

            // Update/Warning Icons
            this.modDataHolder.getUpdateStatus().ifPresent(status -> {
               if (status.isOutdated())
                   this.renderUpdateIcon(graphics);
            });
        }
    }

    private void renderUpdateIcon(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        float offset = Mth.sin((PaCoClientTicker.getGlobal() + PaCoClientTicker.getPartialTick()) * 0.3F);
        graphics.pose().translate(0F, offset, 0F);
        graphics.blit(PaCoScreen.TEXTURE, this.getX() + 18, this.getY() - 1, 0, 170, 8, 10);
        graphics.pose().popPose();




        //TODO add method for warnings or make a smart pos calculation thingy that determines where to place the icons
        graphics.blit(PaCoScreen.TEXTURE, this.getX() + 10, this.getY() - 1, 8, 170, 8, 10);
    }

    @Override
    public int getY() {
        int y = super.getY();
        if (this.screen.modsScrollBar != null)
            return Math.round((float) (y - this.screen.modsScrollBar.getValue()));
        return y;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            // Moves Button into bounds if it's partially cut of.
            this.moveButtonIntoFocus(false);
        }
    }

    /** Moves the {@link ModButton} up/down and adds padding if needed to avoid the gradient, or being hidden post resizing the {@link PaCoScreen}. */
    public void moveButtonIntoFocus(boolean moveToTop) {
        if (this.screen.modsScrollBar == null) return;
        int padding = 16; // We use padding so because the gradient would interfere with the buttons otherwise.
        if (this.getY() < this.screen.modButtonsStart + padding) { // Top Area
            int pixels = this.screen.modButtonsStart - this.getY();
            this.screen.modsScrollBar.setValue(this.screen.modsScrollBar.getValue() - (pixels + padding));
        } else if (this.getY() + this.getHeight() > this.screen.menuHeightStop - padding) { // Bottom Area
            int pixels = this.getY() + this.getHeight() - this.screen.menuHeightStop;
            this.screen.modsScrollBar.setValue(this.screen.modsScrollBar.getValue() + (pixels + padding));
            // Used to move the button to the top of the list if possible
            if (moveToTop)
                this.screen.modsScrollBar.setValue(this.screen.modsScrollBar.getValue() + (this.screen.modButtonsPanelLength - this.getHeight() - padding * 2));
        }
    }

    @Override
    public void onPress() {
        if (this.screen.selectedModButton != this) {
            // We unselect the currently selected mod button if it's a different one
            if (this.screen.selectedModButton != null)
                this.screen.selectedModButton.setSelected(false);
            this.setSelected(true);
            this.screen.selectedModButton = this;
        } else {
            // We unselect this button if it's already selected
            this.setSelected(false);
            this.screen.selectedModButton = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PaCoGuiUtils.isMouseWithin(mouseX, mouseY, 5, this.screen.modButtonsStart, this.screen.modButtonWidth, this.screen.modButtonsPanelLength))
            return super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    private void renderModIcon(ModDataHolder holder, GuiGraphics graphics, int posX, int posY, int size) {
        String modId = holder.getModId();
        Pair<ResourceLocation, Pair<Integer, Integer>> iconData = this.getIconData(modId);
        if (iconData != null) {
            // If the ResourceLocation isn't null we render the icon.
            ResourceLocation resourceLocation = iconData.getFirst();
            Pair<Integer, Integer> dimensions = iconData.getSecond();
            graphics.blit(resourceLocation, posX, posY, size, size, 0, 0, dimensions.getFirst(), dimensions.getSecond(), dimensions.getFirst(), dimensions.getSecond());
        } else {
            // Otherwise we render a predefined or missing icon texture.
            ResourceLocation rl = MOD_ICONS.get(modId);
            if (rl != null) { // If the mod has a predefined icon we use that
                graphics.blit(rl, posX, posY, size, size, 0, 0, 23, 23, 23, 23);
            } else { // If the mod has a missing icon we do some tinting and render the first 2 letters
                // The "rim", this is technically the entire icon, but it's easier to just do it this way
                graphics.blit(MISSING_MOD_ICON, posX, posY, size, size, 0, 0, 23, 23, 23, 23);
                // The centerpiece
                int color = PaCoColor.colorFromHSV(Math.abs(modId.hashCode()) % 360, 0.8F, 0.7F);
                float r = PaCoColor.red(color) / 255F;
                float g = PaCoColor.green(color) / 255F;
                float b = PaCoColor.blue(color) / 255F;
                RenderSystem.setShaderColor(r, g, b, 1F);
                graphics.blit(MISSING_MOD_ICON, posX + 4, posY + 4, 15, 15, 4, 4, 15, 15, 23, 23);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                // The two letters inside the icon
                graphics.pose().pushPose();
                graphics.pose().translate(posX + 11.5F, posY + 7.5F, 0F);
                PaCoGuiUtils.drawCenteredString(graphics, Minecraft.getInstance().font, holder.getModName().substring(0, 2), 0, 0, 0xa0a0a0, true);
                graphics.pose().popPose();
            }
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
        // Checks if the modId is already present in the cache, and grabs the values if so.
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

        // If no icon is already cached, we attempt to load one, and stop if a valid one was found
        // Alternatively if there is no icons (the list is empty) the loop will not get called and null is cached/returned
        for (String iconFile : this.modDataHolder.getModIconFiles()) {
            Pair<ResourceLocation, Pair<Integer, Integer>> result = Services.PLATFORM.loadNativeImage(modId, iconFile, nativeImage -> {
                // If the icon fails to load, or the icon aspect ratio isn't 1:1 null is provided
                if (nativeImage == null || nativeImage.getWidth() != nativeImage.getHeight())
                    return null;

                // We determine whether the image should be blurred, we check if a property has been specified.
                // If not we manually determine it, 92 Because its 4x the native size of 23x23
                boolean blurIcon = this.modDataHolder.getBlurModIcon().orElse(nativeImage.getWidth() > 92);
                DynamicTexture dynamicTexture = null;
                try {
                    dynamicTexture = new DynamicTexture(nativeImage) {
                        @Override
                        public void upload() {
                            this.bind();
                            NativeImage image = this.getPixels();
                            this.getPixels().upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blurIcon, false, false, false);
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
                    return null; // Return null to indicate failure for this icon
                }
            });
            // If we found a valid icon, we return it
            // This will stop the loop as we don't need to look at other entries
            if (result != null)
                return result;
        }
        // If we went through all the icons and none succeeded or were valid,
        // we cache null,null to indicate that and return null
        this.screen.iconManager.cacheModIcon(modId, null, null);
        return null;
    }

    public ModDataHolder getModDataHolder() {
        return this.modDataHolder;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
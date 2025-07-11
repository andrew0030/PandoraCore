package com.github.andrew0030.pandora_core.client.gui.buttons.mod_selection;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.content_panel.PaCoContentPanelManager;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ModButton extends AbstractButton {
    public static final ResourceLocation MISSING_MOD_ICON = new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/missing_mod_icon.png");
    public static final HashMap<String, ResourceLocation> MOD_ICONS = new HashMap<>();
    private final ModDataHolder modDataHolder;
    private final PaCoScreen screen;
    private final boolean isOutdated;
    private final int updateArrowU;
    private final int nameColor;
    private final int versionColor;
    private boolean selected;

    static {
        MOD_ICONS.put("minecraft", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/mc_mod_icon.png"));
        MOD_ICONS.put("forge", new ResourceLocation(PandoraCore.MOD_ID, "textures/gui/forge_mod_icon.png"));
    }

    public ModButton(int x, int y, int width, int height, ModDataHolder modDataHolder, PaCoScreen screen) {
        super(x, y, width, height, Component.literal(modDataHolder.getModName()));
        this.modDataHolder = modDataHolder;
        this.screen = screen;
        this.isOutdated = this.modDataHolder.isOutdated();
        this.updateArrowU = this.isOutdated ? this.modDataHolder.getUpdateInfo().get().getSource() == UpdateInfo.Source.MODRINTH ? 8 : 0 : 0;
        this.nameColor = this.modDataHolder.hasModWarnings() ? PaCoScreen.SOFT_RED_TEXT_COLOR : PaCoColor.WHITE;
        this.versionColor = this.isOutdated ?
                this.modDataHolder.getUpdateInfo().get().getSource() == UpdateInfo.Source.URL ?
                        PaCoColor.color(200, 150, 10) :
                        PaCoColor.color(10, 200, 90) :
                PaCoColor.color(130, 130, 130);
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
            int iconOffset = this.modDataHolder.hasModWarnings() ? 9 : 0;
            int nameWidth = Minecraft.getInstance().font.width(name);
            if (nameWidth > availableWidth - iconOffset) {
                name = Minecraft.getInstance().font.plainSubstrByWidth(name, availableWidth - iconOffset - Minecraft.getInstance().font.width("...")).trim().concat("...");
            }
            graphics.drawString(Minecraft.getInstance().font, name, this.getX() + this.getHeight() + 2 + iconOffset, this.getY() + 3, this.nameColor, false);
            // Mod Version
            String version = "v" + this.modDataHolder.getModVersion();
            iconOffset = this.modDataHolder.isOutdated() ? 9 : 0;
            int versionWidth = Minecraft.getInstance().font.width(version);
            if (versionWidth > availableWidth - iconOffset) {
                version = Minecraft.getInstance().font.plainSubstrByWidth(version, availableWidth - iconOffset - Minecraft.getInstance().font.width("...")).concat("...");
            }
            graphics.drawString(Minecraft.getInstance().font, version, this.getX() + this.getHeight() + 2 + iconOffset, this.getY() + 14, this.versionColor, false);

            // Warning/Update Icons
            if (this.modDataHolder.hasModWarnings())
                this.renderWarningIcon(graphics);
            if (this.isOutdated)
                this.renderUpdateIcon(graphics);
        }
    }

    private void renderWarningIcon(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        int idx = Math.max(this.screen.filteredMods.indexOf(this.getModDataHolder()), 0);
        float offset = -Mth.abs(Mth.sin((PaCoClientTicker.getGlobal() + PaCoClientTicker.getPartialTick() + idx * 2) * 0.16F));
        graphics.pose().translate(0F, offset, 0F);
        graphics.blit(PaCoScreen.TEXTURE, this.getX() + this.getHeight() + 2, this.getY() + 2, 16, 170, 8, 10);
        graphics.pose().popPose();
    }

    private void renderUpdateIcon(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        int idx = Math.max(this.screen.filteredMods.indexOf(this.getModDataHolder()), 0);
        float offset = -Mth.abs(Mth.sin((PaCoClientTicker.getGlobal() + PaCoClientTicker.getPartialTick() + idx * 2) * 0.16F));
        graphics.pose().translate(0F, offset, 0F);
        graphics.blit(PaCoScreen.TEXTURE, this.getX() + this.getHeight() + 2, this.getY() + 13, this.updateArrowU, 170, 8, 10);
        graphics.pose().popPose();
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
        ModButton currentButton = this.screen.selectedModButton;
        PaCoContentPanelManager manager = this.screen.contentPanelManager;
        manager.resetBounds();
        if (currentButton == this) {
            // We unselect this button if it's already selected
            manager.clearElements();
            this.screen.setContentScrollBar(null);
            this.setSelected(false);
            this.screen.selectedModButton = null;
        } else {
            // We unselect the currently selected mod button if it's a different one
            if (currentButton != null)
                currentButton.setSelected(false);
            // We select the new button
            this.setSelected(true);
            this.screen.selectedModButton = this;
            this.screen.setContentScrollBar(null);
            manager.buildContentPanel(this.getModDataHolder());
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
        Pair<ResourceLocation, Pair<Integer, Integer>> iconData = this.screen.imageManager.getImageData(
                modId,
                this.screen.imageManager::getCachedIcon,
                this.screen.imageManager::cacheIcon,
                holder.getModIconFiles(),
                1.0F,
                (imgWidth, ingHeight) -> this.modDataHolder.getBlurModIcon().orElse(imgWidth > 92),
                "icon"
        );
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
                int color = PaCoColor.colorFromHSV(Math.abs(modId.hashCode()) % 360, 0.8F, 0.7F); //TODO: adjust colors or text
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
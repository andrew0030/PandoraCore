package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.client.utils.gui.PaCoGuiUtils;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolderEntry;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BooleanButtonEntry extends BaseConfigEntry {

    public BooleanButtonEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        super(screen, node, x, y, width, height);
        ConfigDataHolderEntry holder = (ConfigDataHolderEntry) node.getDataHolder();
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        Button widget = new Button(this, Component.literal("TODO")); //TODO fix narration
        // Sets the value to the current value from the config
        try {
            widget.setValue((boolean) holder.getField().get(null)); // TODO probably abstract value retrieval
        } catch (Exception ignored) {}
        // Lastly we add the widget to the list
        this.widgets.add(widget);
    }

    private static class Button extends AbstractWidget {
        private final BaseConfigEntry entry;
        private final ConfigDataHolder holder;
        private final PaCoConfigManager manager;
        private boolean value;

        public Button(BaseConfigEntry entry, Component message) {
            super(entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), message);
            this.entry = entry;
            this.holder = entry.node.getDataHolder();
            this.manager = entry.screen.getManager();
        }

        // NOTE: The code in this block should be implemented by all widgets used for config entries, the methods
        //       ensure that the widgets move along the config entries, and aren't clickable when out of bounds!
        // #########################################################################################################
        @Override
        public int getY() {
            return super.getY() + this.entry.getScrollOffset();
        }
        @Override
        public boolean isHovered() {
            return this.entry.isHovered() && super.isHovered();
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.entry.screen.isMouseInEntriesBounds(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
        }
        // #########################################################################################################

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int posX = this.getX() + this.width - 60;
            int posY = this.getY() + 4;

            // TODO replace these placeholder "textures" with an actual texture
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.blitNineSliced(WIDGETS_LOCATION, this.getX() + getWidth() - 120, this.getY(), 120, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            PaCoGuiUtils.drawCenteredString(graphics, minecraft.font, this.value ? "True" : "False", posX, posY, PaCoColor.WHITE, true);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.setValue(!this.value);

            // TODO improve how config values are set (should be a bulk operation)
            String key = this.holder.getPath();
            manager.getConfig().set(key, this.value);
            manager.correctIfNeeded(true);
        }

        private void setValue(boolean value) {
            this.value = value;
        }

        private int getTextureY() {
            int i = 1;
            if (!this.active) {
                i = 0;
            } else if (this.isHoveredOrFocused()) {
                i = 2;
            }

            return 46 + i * 20;
        }
    }
}
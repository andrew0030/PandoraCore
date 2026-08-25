package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BooleanEntry extends BaseConfigEntry<Boolean> {

    public BooleanEntry(PaCoConfigScreen screen, ConfigTreeNode node, int y, int height, boolean hasScrollbar) {
        super(screen, node, y, height, hasScrollbar);
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        Checkbox<Boolean> widget = new Checkbox<>(this, Component.literal("TODO")); //TODO fix narration
        // Sets the value to the current value from the config
        widget.setValue(this.getValue());
        // Lastly we add the widget to the list
        this.widgets.add(widget);
    }

    private static class Checkbox<T> extends AbstractWidget {
        private final BaseConfigEntry<T> entry;
        private final PaCoConfigManager manager;
        private boolean value;

        public Checkbox(BaseConfigEntry<T> entry, Component message) {
            super(entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), message);
            this.entry = entry;
            this.manager = this.entry.screen.getManager();
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
            int posX = this.getX() + this.width - 20;
            int posY = this.getY() + 2;
            // TODO replace these placeholder "textures" with an actual texture
            int rimColor = this.isHoveredOrFocused() ? PaCoColor.WHITE : PaCoColor.color(180, 180, 180);
            graphics.fill(posX, posY, posX + 12, posY + 12, rimColor);
            graphics.fill(posX + 1, posY + 1, posX + 11, posY + 11, PaCoColor.color(60, 60, 60));
            if (this.value)
                graphics.fill(posX + 2, posY + 2, posX + 10, posY + 10, PaCoColor.color(20, 180, 20));
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.active && this.visible && CommonInputs.selected(keyCode)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onPress();
        }

        private void onPress() {
            this.setValue(!this.value);

            // TODO improve how config values are set (should be a bulk operation)
            String key = this.entry.getDataHolder().getPath();
            manager.getConfig().set(key, this.value);
            manager.correctIfNeeded(true);
        }

        private void setValue(boolean value) {
            this.value = value;
        }
    }
}
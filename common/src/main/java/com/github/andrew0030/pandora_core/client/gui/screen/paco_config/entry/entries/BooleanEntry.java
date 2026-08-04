package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolderEntry;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import com.github.andrew0030.pandora_core.utils.color.PaCoColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BooleanEntry extends BaseConfigEntry {

    public BooleanEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        super(screen, node, x, y, width, height);
        ConfigDataHolderEntry holder = (ConfigDataHolderEntry) node.getDataHolder();
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        Checkbox widget = new Checkbox(x, y, width, height, Component.literal("TODO"), holder, screen.getManager()); //TODO fix narration
        // Sets the value to the current value from the config
        try {
            widget.setValue((boolean) holder.getField().get(null));
        } catch (Exception ignored) {}
        // Lastly we add the widget to the list
        this.widgets.add(widget);
    }

    private static class Checkbox extends AbstractWidget {
        private final ConfigDataHolder holder;
        private final PaCoConfigManager manager;
        private boolean value;

        public Checkbox(int x, int y, int width, int height, Component message, ConfigDataHolderEntry holder, PaCoConfigManager manager) {
            super(x, y, width, height, message);
            this.holder = holder;
            this.manager = manager;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int posX = this.getX() + this.width - 20;
            int posY = this.getY() + 2;
            // TODO replace these placeholder "textures" with an actual texture
            graphics.fill(posX, posY, posX + 12, posY + 12, PaCoColor.color(180, 180, 180));
            graphics.fill(posX + 1, posY + 1, posX + 11, posY + 11, PaCoColor.color(60, 60, 60));
            if (this.value)
                graphics.fill(posX + 2, posY + 2, posX + 10, posY + 10, PaCoColor.color(20, 180, 20));
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
    }
}
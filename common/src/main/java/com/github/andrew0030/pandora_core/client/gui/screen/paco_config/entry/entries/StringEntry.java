package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.edit_boxes.PaCoEditBox;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolder;
import com.github.andrew0030.pandora_core.config.manager.ConfigDataHolderEntry;
import com.github.andrew0030.pandora_core.config.manager.PaCoConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class StringEntry extends BaseConfigEntry {
    private final TextField widget;

    public StringEntry(PaCoConfigScreen screen, ConfigTreeNode node, int x, int y, int width, int height) {
        super(screen, node, x, y, width, height);
        ConfigDataHolderEntry holder = (ConfigDataHolderEntry) node.getDataHolder();
        // Creates the interactable widget
        // TODO maybe improve what kind of data is passed to the widgets? Something to look into after more of the types are implemented!
        this.widget = new TextField(this, Component.literal("TODO")); //TODO fix narration
        this.widget.setForceLineIndicator(true);
        this.widget.setMidpointCharSelection(true);
        // Sets the value to the current value from the config
        try {
            this.widget.setValue((String) holder.getField().get(null));
        } catch (Exception ignored) {}
        // Lastly we add the widget to the list
        this.widgets.add(this.widget);
    }

    @Override
    public void tick() {
        this.widget.tick();
    }

    private static class TextField extends PaCoEditBox {
        private final BaseConfigEntry entry;
        private final ConfigDataHolder holder;
        private final PaCoConfigManager manager;

        public TextField(BaseConfigEntry entry, Component message) {
            super(Minecraft.getInstance().font, entry.getX() + entry.getWidth() - 120, entry.getY() + 1, 119, entry.getHeight() - 2, message);
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
        public void onTextChanged(String newText) {
            String key = this.holder.getPath();
            manager.getConfig().set(key, newText);
            manager.correctIfNeeded(true);
        }
    }
}
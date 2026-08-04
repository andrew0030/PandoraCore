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
        this.widget = new TextField(x, y, width, height, Component.literal("TODO"), holder, screen.getManager()); //TODO fix narration
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
        private final ConfigDataHolder holder;
        private final PaCoConfigManager manager;

        public TextField(int x, int y, int width, int height, Component message, ConfigDataHolderEntry holder, PaCoConfigManager manager) {
            super(Minecraft.getInstance().font, x + width - 120, y + 1, 120, height - 2, message);
            this.holder = holder;
            this.manager = manager;
        }

        @Override
        public void onTextChanged(String newText) {
            String key = this.holder.getPath();
            manager.getConfig().set(key, newText);
            manager.correctIfNeeded(true);
        }
    }
}
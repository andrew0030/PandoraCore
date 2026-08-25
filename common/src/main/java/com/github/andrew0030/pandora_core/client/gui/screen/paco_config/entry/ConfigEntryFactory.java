package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries.BaseConfigEntry;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;

@FunctionalInterface
public interface ConfigEntryFactory {

    // TODO: Maybe adjust the values that are passed to BaseConfigEntry
    BaseConfigEntry<?> create(PaCoConfigScreen screen, ConfigTreeNode node, int y, int height, boolean hasScrollbar);
}
package com.github.andrew0030.pandora_core.client.gui.screen.paco_config.entry.entries;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.tree.ConfigTreeNode;

public class ShortEntry extends NumberEntry<Short> {
    public ShortEntry(PaCoConfigScreen screen, ConfigTreeNode node, int y, int height, boolean hasScrollBar) {
        super(screen, node, y, height, hasScrollBar, Short::parseShort);
    }
}
package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_config.PaCoConfigScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class ConfigSearchEditBox extends PaCoEditBox {
    private final PaCoConfigScreen screen;
    private boolean hidingAllEntries;

    public ConfigSearchEditBox(Font font, int x, int y, int width, int height, Component message, PaCoConfigScreen screen) {
        super(font, x, y, width, height, message);
        this.screen = screen;
    }

    @Override
    public void onTextChanged(String newText) {
        this.screen.refresh();
        boolean hasText = StringUtil.isNullOrEmpty(newText);
        // Triggers a message update for the back button
        if (hasText) this.screen.backButton.getMessage();
        // If the entered text causes no results to be displayed we make it red.
        this.hidingAllEntries = !hasText && this.screen.getVisibleEntries().isEmpty();
        this.setTextColor(this.hidingAllEntries ? PaCoConfigScreen.DARK_RED_TEXT_COLOR : PaCoConfigScreen.DARK_GRAY_TEXT_COLOR);
    }

    /** @return Whether the current text value results in all entries from being hidden. */
    public boolean isHidingAllEntries() {
        return this.hidingAllEntries;
    }
}
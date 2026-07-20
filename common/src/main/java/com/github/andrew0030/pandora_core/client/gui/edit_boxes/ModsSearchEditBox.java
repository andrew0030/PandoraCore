package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.client.gui.screen.paco_main.PaCoScreen;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ModsSearchEditBox extends PaCoEditBox {
    private final PaCoScreen screen;
    private boolean hidingAllMods;

    public ModsSearchEditBox(Font font, int x, int y, int width, int height, Component message, PaCoScreen screen) {
        super(font, x, y, width, height, message);
        this.screen = screen;
    }

    @Override
    public void onTextChanged(String newText) {
        List<ModDataHolder> holders = this.screen.createOrderedModsList();
        this.screen.filteredMods.clear();
        String lowerCaseText = newText == null ? "" : newText.toLowerCase();
        // We filter the mods based on the newText
        List<ModDataHolder> filteredHolders = holders.stream()
                .filter(holder -> lowerCaseText.isEmpty() || holder.getModName().toLowerCase().contains(lowerCaseText))
                .toList();
        // After we filtered the mods we add them to the list and refresh
        this.screen.filteredMods.addAll(filteredHolders);
        this.screen.refresh();

        // If the entered text causes no results to be displayed we make it red.
        this.hidingAllMods = !holders.isEmpty() && filteredHolders.isEmpty();
        this.setTextColor(this.hidingAllMods ? PaCoScreen.DARK_RED_TEXT_COLOR : PaCoScreen.DARK_GRAY_TEXT_COLOR);
    }

    /**
     * Whether the current text value results in all Mods from being hidden.<br/>
     * Note: if there are no Mods (for example warnings filter without any mods that have warnings
     * or update filter without any found updates) this will return false.
     */
    public boolean isHidingAllMods() {
        return this.hidingAllMods;
    }
}
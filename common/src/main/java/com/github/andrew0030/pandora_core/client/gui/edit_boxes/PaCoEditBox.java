package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import com.github.andrew0030.pandora_core.utils.ModDataHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

import java.util.List;

public class PaCoEditBox extends EditBox implements IPaCoEditBox {
    private final PaCoScreen screen;

    public PaCoEditBox(Font font, int x, int y, int width, int height, Component message, PaCoScreen screen) {
        super(font, x, y, width, height, message);
        this.screen = screen;
    }

    @Override
    public boolean pandoraCore$hideBackground() {
        return true;
    }

    @Override
    public boolean pandoraCore$hideRim() {
        return true;
    }

    @Override
    public void pandoraCore$onValueChange(String newText) {
        List<ModDataHolder> holders = this.screen.createOrderedModsList();
        this.screen.filteredMods.clear(); // We clear the current mods list.

        if (StringUtil.isNullOrEmpty(newText)) {//TODO do we need to trim?
            this.screen.filteredMods.addAll(holders);
        } else {
            for (ModDataHolder holder : holders)
                if (holder.getModNameOrId().toLowerCase().contains(newText.toLowerCase()))
                    this.screen.filteredMods.add(holder);
        }
        this.screen.refresh();
    }
}
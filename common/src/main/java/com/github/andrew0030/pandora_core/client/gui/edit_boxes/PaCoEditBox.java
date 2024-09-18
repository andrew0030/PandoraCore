package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaCoEditBox extends EditBox implements IPaCoEditBox {
    private final Font font;
    private final PaCoScreen screen;

    public PaCoEditBox(Font font, int x, int y, int width, int height, Component message, PaCoScreen screen) {
        super(font, x, y, width, height, message);
        this.font = font;
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
        this.screen.filteredMods.clear();
        String lowerCaseText = newText == null ? "" : newText.toLowerCase();
        // We filter the mods based on the newText
        List<ModDataHolder> filteredHolders = holders.stream()
                .filter(holder -> lowerCaseText.isEmpty() || holder.getModName().toLowerCase().contains(lowerCaseText))
                .toList();
        // After we filtered the mods we add them to the list and refresh
        this.screen.filteredMods.addAll(filteredHolders);
        this.screen.refresh();
    }

    @Override
    public void setHint(@NotNull Component hint) {
        String newHint = this.font.plainSubstrByWidth(hint.getString(), this.getWidth() - 4);
        super.setHint(Component.literal(newHint));
    }
}
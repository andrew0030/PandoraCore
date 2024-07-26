package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.gui.screen.PaCoScreen;
import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;

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
    public void insertText(@NotNull String textToWrite) {
        super.insertText(textToWrite);
        List<String> mods = List.copyOf(PandoraCore.getPaCoManagedMods());
        this.screen.filteredMods.clear();

        if (StringUtil.isNullOrEmpty(this.getValue())) {
            this.screen.filteredMods.addAll(mods);

            System.out.println("All mods added: " + this.screen.filteredMods);
        } else {
            for (String mod : mods)
                if (mod.contains(this.getValue()))
                    this.screen.filteredMods.add(mod);

            System.out.println("Filtered: " + this.screen.filteredMods);
        }
    }
}
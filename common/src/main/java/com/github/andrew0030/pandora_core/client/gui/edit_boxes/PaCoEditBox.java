package com.github.andrew0030.pandora_core.client.gui.edit_boxes;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PaCoEditBox extends EditBox implements IPaCoEditBox {

    public PaCoEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    @Override
    public boolean pandoraCore$hideBackground() {
        return true;
    }

    @Override
    public boolean pandoraCore$hideRim() {
        return true;
    }
}
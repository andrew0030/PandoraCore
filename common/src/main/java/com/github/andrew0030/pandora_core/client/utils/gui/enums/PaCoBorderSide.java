package com.github.andrew0030.pandora_core.client.utils.gui.enums;

import com.github.andrew0030.pandora_core.utils.color.PaCoColor;

// TODO write javadoc for this
public enum PaCoBorderSide {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT;

    private int size;
    private int color;

    PaCoBorderSide() {
        this.size = 1;
        this.color = PaCoColor.color(207, 207, 196);
    }

    public int getSize() {
        return this.size;
    }

    public PaCoBorderSide setSize(int size) {
        this.size = size;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public PaCoBorderSide setColor(int color) {
        this.color = color;
        return this;
    }
}
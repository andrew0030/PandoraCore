package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;

public class InstanceDataElement {
    public final String name;
    public final int size;
    public final NumericPrimitive type;

    public InstanceDataElement(String name, NumericPrimitive type, int count) {
        this.name = name;
        this.type = type;
        this.size = count;
    }

    public int bytes() {
        return size * type.size;
    }
}

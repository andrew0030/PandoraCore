package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;

public class InstanceDataElement {
    public final String name;
    public final int size;
    public final int components;
    public final NumericPrimitive type;
    public final boolean normalize;

    public InstanceDataElement(String name, NumericPrimitive type, int count) {
        this.name = name;
        this.type = type;
        this.size = count;
        this.components = 1;
        this.normalize = false;
    }

    public InstanceDataElement(String name, NumericPrimitive type, int count, int components) {
        this.name = name;
        this.type = type;
        this.size = count;
        this.components = components;
        this.normalize = false;
    }

    public InstanceDataElement(String name, NumericPrimitive type, int count, int components, boolean normalize) {
        this.name = name;
        this.type = type;
        this.size = count;
        this.components = components;
        this.normalize = normalize;
    }

    public int bytes() {
        return size * type.size * components;
    }
}

package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program;

import com.mojang.blaze3d.shaders.AbstractUniform;

public abstract class BaseProgram {
    public abstract void bind();

    public abstract void close();

    public abstract void clear();

    public abstract void upload();

    public abstract AbstractUniform getUniform(String name, int type, int count);

    public abstract int getAttributeLocation(String name);
}

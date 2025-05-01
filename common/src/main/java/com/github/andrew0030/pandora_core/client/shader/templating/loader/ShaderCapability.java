package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import net.minecraft.resources.ResourceLocation;

public class ShaderCapability {
    public final ResourceLocation name;
    public final String debugString;

    public ShaderCapability(ResourceLocation name, String debugString) {
        this.name = name;
        this.debugString = debugString;
    }
}

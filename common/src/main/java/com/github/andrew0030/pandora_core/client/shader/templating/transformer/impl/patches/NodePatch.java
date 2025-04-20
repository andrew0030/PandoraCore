package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.patches;

import io.github.ocelot.glslprocessor.api.node.GlslNode;

public abstract class NodePatch {
    public abstract void accept(GlslNode nd);
}

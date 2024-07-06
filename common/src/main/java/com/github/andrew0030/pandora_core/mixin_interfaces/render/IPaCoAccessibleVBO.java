package com.github.andrew0030.pandora_core.mixin_interfaces.render;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

public interface IPaCoAccessibleVBO {
    VertexFormat.Mode pandoraCore$mode();
    int pandoraCore$indexCount();
    VertexFormat.IndexType pandoraCore$indexType();
    VertexBuffer.Usage pandoraCore$usage();
}

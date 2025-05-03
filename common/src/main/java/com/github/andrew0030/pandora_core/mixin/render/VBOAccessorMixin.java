package com.github.andrew0030.pandora_core.mixin.render;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexBuffer.class)
public class VBOAccessorMixin implements IPaCoAccessibleVBO {
    @Shadow
    private VertexFormat.IndexType indexType;

    @Shadow
    private int indexCount;

    @Shadow
    private VertexFormat.Mode mode;

    @Shadow @Final private VertexBuffer.Usage usage;

    @Shadow private int vertexBufferId;

    @Override
    public VertexFormat.Mode pandoraCore$mode() {
        return mode;
    }

    @Override
    public int pandoraCore$indexCount() {
        return indexCount;
    }

    @Override
    public VertexFormat.IndexType pandoraCore$indexType() {
        return indexType;
    }

    @Override
    public VertexBuffer.Usage pandoraCore$usage() {
        return usage;
    }

    @Override
    public int pandoraCore$vertexId() {
        return vertexBufferId;
    }
}

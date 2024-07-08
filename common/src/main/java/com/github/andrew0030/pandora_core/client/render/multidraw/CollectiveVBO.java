package com.github.andrew0030.pandora_core.client.render.multidraw;

import com.github.andrew0030.pandora_core.client.render.instancing.InstanceData;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.instancing.InstancedVBO;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.lwjgl.opengl.*;

import java.util.Map;

public class CollectiveVBO extends InstancedVBO {
    InstanceFormat format;

    public CollectiveVBO(Usage usage, InstanceFormat format) {
        super(usage, format);
        this.format = format;
    }

    private final Map<String, MultidrawBufferBuilder.MeshRange> ranges = new Object2ObjectRBTreeMap<>();

    public void upload(MultidrawBufferBuilder builder) {
        BufferBuilder.RenderedBuffer buffer = builder.direct.end();
        ranges.clear();
        for (MultidrawBufferBuilder.MeshRange range : builder.ranges)
            ranges.put(range.name, range);
        super.upload(buffer);
    }

    int[] bases = new int[0];
    int[] counts = bases;

    public void defineRanges(
            int[] bases,
            int[] counts
    ) {
        this.bases = bases;
        this.counts = counts;
    }

    public void defineRanges(
            MultidrawBufferBuilder.MeshRange... ranges
    ) {
        if (bases.length != ranges.length) {
            bases = new int[ranges.length];
            counts = new int[ranges.length];
        }
        for (int i = 0; i < ranges.length; i++) {
            MultidrawBufferBuilder.MeshRange range = ranges[i];
            bases[i] = range.start;
            counts[i] = range.end - range.start;
        }
    }

    @Override
    public void draw() {
        // TODO: batching system for hardware that doesn't support instancing
        //       requires uniform injection to work though
//        IPaCoAccessibleVBO accessibleVBO = (IPaCoAccessibleVBO) this;
//        GL32C.glMultiDrawElementsBaseVertex(
//                accessibleVBO.pandoraCore$mode().asGLMode,
//                counts,
//                accessibleVBO.pandoraCore$indexType().asGLType,
//                null,
//                bases
//        );
        super.draw();
    }
}
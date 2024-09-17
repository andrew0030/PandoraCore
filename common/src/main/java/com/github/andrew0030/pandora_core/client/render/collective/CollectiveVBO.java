package com.github.andrew0030.pandora_core.client.render.collective;

import com.github.andrew0030.pandora_core.client.render.instancing.InstanceData;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.instancing.InstancedVBO;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.lwjgl.opengl.*;

import java.util.Map;

public class CollectiveVBO extends InstancedVBO {
    InstanceFormat format;

    public CollectiveVBO(Usage usage, InstanceFormat format) {
        super(usage, format);
        this.format = format;
    }

    public CollectiveVBO(AccelerationUsage pUsage, InstanceFormat format) {
        super(pUsage, format);
        this.format = format;
    }

    private final Map<String, CollectiveBufferBuilder.MeshRange> ranges = new Object2ObjectRBTreeMap<>();

    public void upload(CollectiveBufferBuilder builder) {
        BufferBuilder.RenderedBuffer buffer = builder.direct.end();
        ranges.clear();
        for (CollectiveBufferBuilder.MeshRange range : builder.ranges)
            ranges.put(range.name(), range);
        super.upload(buffer);
    }

    CollectiveDrawData data;
    Object2ObjectArrayMap<CollectiveBufferBuilder.MeshRange, Pair<InstanceData, Integer>> datas = new Object2ObjectArrayMap<>();

    public void setupData(CollectiveDrawData data) {
        this.data = data;
        datas.clear();
        data.active.forEach((k, v) -> {
            datas.put(k, Pair.of(v, v.drawCount()));
        });
    }

    @Override
    public void draw() {
        // TODO: batching system for hardware that doesn't support instancing
        //       requires uniform injection to work though
        IPaCoAccessibleVBO accessibleVBO = (IPaCoAccessibleVBO) this;
        int mode = accessibleVBO.pandoraCore$mode().asGLMode;
        int type = accessibleVBO.pandoraCore$indexType().asGLType;
        for (Map.Entry<CollectiveBufferBuilder.MeshRange, Pair<InstanceData, Integer>> value : datas.entrySet()) {
            bindData(value.getValue().getFirst());
            GL33C.nglDrawElementsInstancedBaseVertex(
                    mode,
                    value.getKey().end() - value.getKey().start(),
                    type,
                    0L,
                    value.getValue().getSecond(),
                    value.getKey().start()
            );
        }
    }
}

package com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class InstanceRenderer<T, Y> {
    public final InstanceFormat format;

    public InstanceRenderer(InstanceFormat format) {
        this.format = format;
    }

    public int getViewDistance() {
        return 64;
    }

    public abstract boolean shouldRender(T object, Vec3 pCameraPos);

    public void render(Level level, T object, Y pos, float pct, Vec3 cameraPos) {
        if (level instanceof PacoInstancingLevel instancingLevel) {
            BatchData data = instancingLevel.getManager().getData(this);
//            if (data == null) data = makeData();
            if (data == null) data = new BatchData(this::makeData);
            render(level, object, pos, data, pct, cameraPos);
            instancingLevel.getManager().markForFrame(this, data);
        }
    }

    public abstract void render(Level level, T object, Y pos, BatchData data, float pct, Vec3 cameraPos);

    public abstract void flush(Level level, BatchData data);

    public CollectiveDrawData makeData() {
        return new CollectiveDrawData(format, 256, VertexBuffer.Usage.DYNAMIC);
    }
}

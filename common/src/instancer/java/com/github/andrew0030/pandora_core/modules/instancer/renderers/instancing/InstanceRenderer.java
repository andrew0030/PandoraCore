package com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.world.phys.Vec3;

public abstract class InstanceRenderer<M extends InstancingEnvironment, T, Y> {
    public final InstanceFormat format;

    public InstanceRenderer(InstanceFormat format) {
        this.format = format;
    }

    public int getViewDistance() {
        return 64;
    }

    public abstract boolean shouldRender(T object, Vec3 pCameraPos);

    public void render(M environment, T object, Y pos, float pct, Vec3 cameraPos) {
	    BatchData data = environment.getManager().getData(this);
	    if (data == null) data = new BatchData(this::makeData);
	    render(environment, object, pos, data, pct, cameraPos);
	    environment.getManager().markForFrame(this, data);
    }

    public abstract void render(M level, T object, Y pos, BatchData data, float pct, Vec3 cameraPos);

    public abstract void flush(M level, BatchData data);

    public CollectiveDrawData makeData() {
        return new CollectiveDrawData(format, 256, VertexBuffer.Usage.DYNAMIC);
    }
}

package com.github.andrew0030.pandora_core.client.render.renderers.instancing;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.instancing.engine.PacoInstancingLevel;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class InstanceRenderer<T, Y> {
    public final InstanceFormat format;
    // protected instead of public final, since it may need to be swaped out or something
    // donno yet
    protected CollectiveVBO vbo;

    public InstanceRenderer(InstanceFormat format, CollectiveVBO vbo) {
        this.format = format;
        this.vbo = vbo;
    }

    public int getViewDistance() {
        return 64;
    }

    public abstract boolean shouldRender(T object, Vec3 pCameraPos);

    public void render(Level level, T object, Y pos) {
        if (level instanceof PacoInstancingLevel instancingLevel) {
            CollectiveDrawData data = instancingLevel.getManager().getData(this);
            if (data == null) data = makeData();
            render(level, object, pos, data);
            instancingLevel.getManager().markForFrame(this, data);
        }
    }

    public abstract void render(Level level, T object, Y pos, CollectiveDrawData data);

    public abstract void flush(Level level, CollectiveDrawData data);

    public CollectiveVBO getVbo() {
        return vbo;
    }

    public CollectiveDrawData makeData() {
        return new CollectiveDrawData(format, 256, VertexBuffer.Usage.DYNAMIC);
    }
}

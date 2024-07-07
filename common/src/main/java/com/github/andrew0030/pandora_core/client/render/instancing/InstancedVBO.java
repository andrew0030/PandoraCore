package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.lwjgl.opengl.*;

public class InstancedVBO extends VertexBuffer {
    InstanceFormat format;
    int instanceBufferId;

    public InstancedVBO(Usage usage, InstanceFormat format) {
        super(usage);
        this.format = format;
        instanceBufferId = GlStateManager._glGenBuffers();
    }

    InstanceData data;
    int count = 0;

    public void bindData(InstanceData data) {
        this.data = data;
        GlStateManager._glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBufferId);
        IPaCoAccessibleVBO accessibleVBO = (IPaCoAccessibleVBO) this;
        count = data.drawCount();
        data.buffer.position(0).limit(count * format.stride);
        RenderSystem.glBufferData(GL20.GL_ARRAY_BUFFER, data.buffer, ((IPaCoAccessibleUsage) (Object) accessibleVBO.pandoraCore$usage()).pandoraCore$id());
        data.buffer.position(0).limit(data.buffer.capacity());
        format.setupState(this.getFormat());
    }

    @Override
    public void upload(BufferBuilder.RenderedBuffer $$0) {
        super.upload($$0);
        getFormat().setupBufferState();
        GlStateManager._glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBufferId);
        format.setupState(this.getFormat());
    }

    @Override
    public void draw() {
        // TODO: batching system for hardware that doesn't support instancing
        //       requires uniform injection to work though
        IPaCoAccessibleVBO accessibleVBO = (IPaCoAccessibleVBO) this;
        GL31.glDrawElementsInstanced(
                accessibleVBO.pandoraCore$mode().asGLMode,
                accessibleVBO.pandoraCore$indexCount(),
                accessibleVBO.pandoraCore$indexType().asGLType,
                0L,
                count
        );
    }
}

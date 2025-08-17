package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.client.render.AcceleratedVBO;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31C;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;
import java.util.List;

public class InstancedVBO extends AcceleratedVBO {
    InstanceFormat format;

    public InstancedVBO(Usage usage, InstanceFormat format) {
        super(usage);
        this.format = format;
    }

    public InstancedVBO(AccelerationUsage pUsage, InstanceFormat format) {
        super(pUsage);
        this.format = format;
    }

    InstanceData data;
    int count = 0;

    protected ShaderWrapper wrapper;

    List<Integer> clientState = new ArrayList<>();

    public void bindData(InstanceData data) {
        for (Integer i : clientState) {
            GL33.glVertexAttribDivisor(
                    i, 0
            );
            GlStateManager._disableVertexAttribArray(i);
        }

        GlStateManager._glBindBuffer(34962, ((IPaCoAccessibleVBO) this).pandoraCore$vertexId());
        this.getFormat().setupBufferState();

        boolean wasData = data == this.data;
        this.data = data;
        GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, data.glBuffer);
        // TODO: I'm pretty sure this is how this works, but I'm not actually sure
//        if (!wasData)
        clientState = format.setupState(this.getFormat(), wrapper);
    }

    public void setDrawCount(int count) {
        this.count = count;
    }

    @Override
    public void draw() {
        // TODO: batching system for hardware that doesn't support instancing
        //       requires uniform injection to work though
        IPaCoAccessibleVBO accessibleVBO = (IPaCoAccessibleVBO) this;
        GL31C.nglDrawElementsInstanced(
                accessibleVBO.pandoraCore$mode().asGLMode,
                accessibleVBO.pandoraCore$indexCount(),
                accessibleVBO.pandoraCore$indexType().asGLType,
                0L,
                count
        );
    }

    public void unbindVBO() {
        for (Integer i : clientState) {
            GL33.glVertexAttribDivisor(
                    i, 0
            );
            GlStateManager._disableVertexAttribArray(i);
        }
        clientState.clear();
    }
}
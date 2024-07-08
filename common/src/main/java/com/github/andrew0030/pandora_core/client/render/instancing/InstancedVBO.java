package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.client.render.AcceleratedVBO;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.*;

public class InstancedVBO extends AcceleratedVBO {
    InstanceFormat format;

    public InstancedVBO(Usage usage, InstanceFormat format) {
        super(usage);
        this.format = format;
    }

    InstanceData data;
    int count = 0;

    public void bindData(InstanceData data) {
        this.data = data;
        GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, data.glBuffer);
        format.setupState(this.getFormat());
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
}
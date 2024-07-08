package com.github.andrew0030.pandora_core.client.render;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.lwjgl.opengl.EXTCompiledVertexArray;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL46;

/**
 * A VBO implementation which takes advantage of GL extensions to try to accelerate rendering
 * TODO: check if this actually does anything to make rendering faster
 */
public class AcceleratedVBO extends VertexBuffer {
    public AcceleratedVBO(Usage pUsage) {
        super(pUsage);
    }

    @Override
    public void upload(BufferBuilder.RenderedBuffer buffer) {
        if (
                SupportChecker.SUPPORT_LOCKED_ARRAYS &&
                        ((IPaCoAccessibleVBO) this).pandoraCore$usage() == Usage.STATIC
        ) {
            int verts = buffer.drawState().vertexCount();
            EXTCompiledVertexArray.glUnlockArraysEXT();
            super.upload(buffer);
            EXTCompiledVertexArray.glLockArraysEXT(0, verts);
        } else super.upload(buffer);
    }
}

package com.github.andrew0030.pandora_core.client.render;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleVBO;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.EXTCompiledVertexArray;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL46;

/**
 * A VBO implementation which takes advantage of GL extensions to try to accelerate rendering
 */
public class AcceleratedVBO extends VertexBuffer {
    AccelerationUsage accelerationUsage;

    public AcceleratedVBO(AccelerationUsage pUsage) {
        super(pUsage.vanilla);
    }

    public AcceleratedVBO(Usage pUsage) {
        super(pUsage);
        accelerationUsage = switch (pUsage) {
            case DYNAMIC -> AccelerationUsage.DYNAMIC;
            default -> AccelerationUsage.STATIC;
        };
    }

    @Override
    public void upload(BufferBuilder.RenderedBuffer buffer) {
        if (
                accelerationUsage == AccelerationUsage.STATIC_LOCKED &&
                        SupportChecker.SUPPORT_LOCKED_ARRAYS &&
                        ((IPaCoAccessibleVBO) this).pandoraCore$usage() == Usage.STATIC
        ) {
            int verts = buffer.drawState().vertexCount();
            EXTCompiledVertexArray.glUnlockArraysEXT();
            super.upload(buffer);
            EXTCompiledVertexArray.glLockArraysEXT(0, verts);
        } else super.upload(buffer);
    }

    public enum AccelerationUsage {
        STATIC_LOCKED(Usage.STATIC),
        STATIC(Usage.STATIC),
        DYNAMIC(Usage.DYNAMIC);

        public final Usage vanilla;
        public final int glId;

        AccelerationUsage(Usage vanilla) {
            this.vanilla = vanilla;
            this.glId = ((IPaCoAccessibleUsage) (Object) vanilla).pandoraCore$id();
        }
    }
}

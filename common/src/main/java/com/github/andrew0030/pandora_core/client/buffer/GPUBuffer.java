package com.github.andrew0030.pandora_core.client.buffer;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GPUBuffer implements AutoCloseable {
    int id;

    public GPUBuffer() {
        this.id = GL30.glGenBuffers();
    }

    public void bind() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, id);
    }

    public void setData(IntBuffer data, VertexBuffer.Usage usage) {
        setData(
                MemoryUtil.memByteBuffer(MemoryUtil.memAddress(data), data.capacity() * 4),
                usage
        );
    }

    public void setData(FloatBuffer data, VertexBuffer.Usage usage) {
        setData(
                MemoryUtil.memByteBuffer(MemoryUtil.memAddress(data), data.capacity() * 4),
                usage
        );
    }

    public void setData(ByteBuffer data, VertexBuffer.Usage usage) {
        GL30.glBufferData(
                GL30.GL_ARRAY_BUFFER,
                data,
                ((IPaCoAccessibleUsage) (Object) usage).pandoraCore$id()
        );
    }

    public void close() {
        if (id != -1) {
            GL30.glDeleteBuffers(id);
            id = -1;
        }
    }

    public static void unbind() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }
}

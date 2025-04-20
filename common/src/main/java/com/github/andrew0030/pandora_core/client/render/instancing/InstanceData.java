package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.client.render.SupportChecker;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.lwjgl.opengl.EXTCompiledVertexArray;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class InstanceData {
    ByteBuffer buffer;
    InstanceFormat format;
    int instances;
    int startWrite = -1;
    int endWrite = -1;
    int glBuffer;
    boolean uploaded = false;
    boolean grown = false;

    int usage;
    static final int STATIC_USAGE = ((IPaCoAccessibleUsage) (Object) VertexBuffer.Usage.STATIC).pandoraCore$id();

    private void grow() {
        ByteBuffer newBuffer = MemoryUtil.memAlloc(buffer.capacity() * 2);
        int pos = buffer.position();
        newBuffer.put(buffer.position(0));
        MemoryUtil.memFree(buffer);
        buffer = newBuffer.position(pos);
        grown = true;
    }

    public InstanceData(InstanceFormat format, int count, VertexBuffer.Usage usage) {
        buffer = MemoryUtil.memAlloc(format.stride * count);
        this.format = format;
        this.instances = count;
        glBuffer = GlStateManager._glGenBuffers();
        this.usage = ((IPaCoAccessibleUsage) (Object) usage).pandoraCore$id();
    }

    public InstanceData writeInstance(int index) {
        if (startWrite != -1)
            upload();

        startWrite = index;
        endWrite = startWrite;
        buffer.limit(buffer.capacity()).position(index * format.stride);
        return this;
    }

    /**
     * Makes sure the buffer is setup to write an instance
     * Puts it at instance 0 if not
     */
    public void ensureInstance() {
        if (startWrite == -1) {
            startWrite = 0;
            endWrite = 0;
        }
    }

    public InstanceData write(byte b) {
        buffer.put(b);
        return this;
    }

    public InstanceData writeShort(short b) {
        buffer.putShort(b);
        return this;
    }

    public InstanceData writeInt(int b) {
        buffer.putInt(b);
        return this;
    }

    public InstanceData writeUV(int uv) {
        buffer.putInt(uv);
//        buffer.putShort((short) (uv & '\uffff'));
//        buffer.putShort((short) (uv >> 16 & '\uffff'));
        return this;
    }

    public InstanceData writeUV(int u, int v) {
        buffer.putShort((short) u);
        buffer.putShort((short) v);
        return this;
    }

    public InstanceData writeUV(short u, short v) {
        buffer.putShort(u);
        buffer.putShort(v);
        return this;
    }

    public InstanceData writeFloat(float b) {
        buffer.putFloat(b);
        return this;
    }

    public InstanceData writeFloat(float x, float y) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        return this;
    }

    public InstanceData writeFloat(float x, float y, float z) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        return this;
    }

    public InstanceData writeFloat(float x, float y, float z, float w) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
        return this;
    }

    public InstanceData writeDouble(double b) {
        buffer.putDouble(b);
        return this;
    }

    public InstanceData writeLong(long b) {
        buffer.putLong(b);
        return this;
    }

    public InstanceData finishInstance() {
        if (buffer.position() != (endWrite + 1) * format.stride)
            throw new RuntimeException("Instance not filled.");
        endWrite++;

        if ((endWrite + 1) * format.stride > buffer.capacity()) {
            grow();
        }

        return this;
    }

    public void close() {
        GlStateManager._glDeleteBuffers(glBuffer);
        MemoryUtil.memFree(buffer);
    }

    public int drawCount() {
        return endWrite;
    }

    public void upload() {
        // don't upload anything if there is nothing to upload
        // if it attempts to upload while there is nothing to upload, the game crashes
        if (startWrite == -1) return;

        GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, glBuffer);
        if (!uploaded || (startWrite == 0 && endWrite == instances) || grown) {
            buffer.position(0).limit(buffer.capacity());
            GlStateManager._glBufferData(
                    GL30.GL_ARRAY_BUFFER,
                    buffer,
                    usage
            );
            uploaded = true;
            grown = false;
        } else {
            buffer.position(startWrite * format.stride);
            buffer.limit(endWrite * format.stride);
            GL30.glBufferSubData(
                    GL30.GL_ARRAY_BUFFER,
                    startWrite * format.stride,
                    buffer
            );
        }
        GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        startWrite = -1;
        endWrite = -1;
        buffer.position(0).limit(buffer.capacity());
    }
}

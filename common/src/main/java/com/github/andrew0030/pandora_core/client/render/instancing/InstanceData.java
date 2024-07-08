package com.github.andrew0030.pandora_core.client.render.instancing;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleUsage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
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

    int usage;

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

    public void write(byte b) {
        buffer.put(b);
    }

    public void writeShort(short b) {
        buffer.putShort(b);
    }

    public void writeInt(int b) {
        buffer.putInt(b);
    }

    public void writeUV(int uv) {
        buffer.putInt(uv);
//        buffer.putShort((short) (uv & '\uffff'));
//        buffer.putShort((short) (uv >> 16 & '\uffff'));
    }

    public void writeUV(int u, int v) {
        buffer.putShort((short) u);
        buffer.putShort((short) v);
    }

    public void writeUV(short u, short v) {
        buffer.putShort(u);
        buffer.putShort(v);
    }

    public void writeFloat(float b) {
        buffer.putFloat(b);
    }

    public void writeFloat(float x, float y) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    public void writeFloat(float x, float y, float z) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
    }

    public void writeFloat(float x, float y, float z, float w) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
    }

    public void writeDouble(double b) {
        buffer.putDouble(b);
    }

    public void writeLong(long b) {
        buffer.putLong(b);
    }

    public void finishInstance() {
        if (buffer.position() != (endWrite + 1) * format.stride)
            throw new RuntimeException("Instance not filled.");
        endWrite++;
    }

    public void close() {
        GlStateManager._glDeleteBuffers(glBuffer);
        MemoryUtil.memFree(buffer);
    }

    public int drawCount() {
        return endWrite;
    }

    public void upload() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, glBuffer);
        if (!uploaded || (startWrite == 0 && endWrite == instances)) {
            buffer.position(0).limit(buffer.capacity());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, usage);
            uploaded = true;
        } else {
            buffer.position(startWrite * format.stride);
            buffer.limit(endWrite * format.stride);
            GL30.glBufferSubData(
                    GL30.GL_ARRAY_BUFFER,
                    startWrite * format.stride,
                    buffer
            );
        }
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        startWrite = -1;
        endWrite = -1;
        buffer.position(0).limit(buffer.capacity());
    }
}

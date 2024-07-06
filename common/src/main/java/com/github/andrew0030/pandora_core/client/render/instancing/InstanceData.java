package com.github.andrew0030.pandora_core.client.render.instancing;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class InstanceData {
    ByteBuffer buffer;
    InstanceFormat format;
    int instances;

    public InstanceData(InstanceFormat format, int count) {
        buffer = MemoryUtil.memAlloc(format.stride * count);
        this.format = format;
        this.instances = count;
    }

    public InstanceData writeInstance(int index) {
        buffer.position(index * format.stride);
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

    public void writeFloat(float b) {
        buffer.putFloat(b);
    }

    public void writeDouble(double b) {
        buffer.putDouble(b);
    }

    public void writeLong(long b) {
        buffer.putLong(b);
    }

    public void close() {
        MemoryUtil.memFree(buffer);
    }

    public int drawCount() {
        return buffer.position() / format.stride;
    }
}

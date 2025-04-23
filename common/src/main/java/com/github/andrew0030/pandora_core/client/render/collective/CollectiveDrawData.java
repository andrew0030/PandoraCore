package com.github.andrew0030.pandora_core.client.render.collective;

import com.github.andrew0030.pandora_core.client.render.instancing.InstanceData;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.joml.Matrix3f;

import java.util.HashMap;
import java.util.HashSet;

public class CollectiveDrawData {
    HashMap<CollectiveBufferBuilder.MeshRange, InstanceData> datas = new HashMap<>();
    InstanceFormat format;
    int count;
    VertexBuffer.Usage usage;
    CollectiveBufferBuilder.MeshRange activeRange;
    InstanceData writing;
    HashSet<InstanceData> written = new HashSet<>();
    HashMap<CollectiveBufferBuilder.MeshRange, InstanceData> active = new HashMap<>();

    public CollectiveDrawData(InstanceFormat format, int count, VertexBuffer.Usage usage) {
        this.format = format;
        this.count = count;
        this.usage = usage;
    }

    public void deactivate() {
        active.clear();
    }

    public void activateData() {
        active.put(activeRange, writing);
    }

    public CollectiveDrawData writeMesh(CollectiveBufferBuilder.MeshRange range) {
        writing = datas.get(range);
        activeRange = range;
        if (writing == null) {
            datas.put(range, writing = new InstanceData(
                    format, count, usage
            ));
        }
        written.add(writing);
        return this;
    }

    public CollectiveDrawData writeInstance(int instance) {
        writing.writeInstance(instance);
        return this;
    }

    /**
     * Makes sure the buffer is setup to write an instance
     * Puts it at instance 0 if not
     */
    public void ensureInstance() {
        writing.ensureInstance();
    }

    public CollectiveDrawData write(byte b) {
        writing.write(b);
        return this;
    }

    public CollectiveDrawData writeShort(short b) {
        writing.writeShort(b);
        return this;
    }

    public CollectiveDrawData writeInt(int b) {
        writing.writeInstance(b);
        return this;
    }

    public CollectiveDrawData writeUV(int uv) {
        writing.writeUV(uv);
        return this;
    }

    public CollectiveDrawData writeUV(int u, int v) {
        writing.writeUV(u, v);
        return this;
    }

    public CollectiveDrawData writeUV(short u, short v) {
        writing.writeUV(u, v);
        return this;
    }

    public CollectiveDrawData writeFloat(float b) {
        writing.writeFloat(b);
        return this;
    }

    public CollectiveDrawData writeFloat(float x, float y) {
        writing.writeFloat(x, y);
        return this;
    }

    public CollectiveDrawData writeFloat(float x, float y, float z) {
        writing.writeFloat(x, y, z);
        return this;
    }

    public CollectiveDrawData writeFloat(float x, float y, float z, float w) {
        writing.writeFloat(x, y, z, w);
        return this;
    }

    public CollectiveDrawData writeMatrix(Matrix3f matrix3f) {
        writing.writeMatrix(matrix3f);
        return this;
    }

    public CollectiveDrawData writeDouble(double b) {
        writing.writeDouble(b);
        return this;
    }

    public CollectiveDrawData writeLong(long b) {
        writing.writeLong(b);
        return this;
    }

    public CollectiveDrawData finishInstance() {
        writing.finishInstance();
        return this;
    }

    public void close() {
        for (InstanceData value : datas.values()) value.close();
    }

    public void upload() {
        for (InstanceData instanceData : written) {
            instanceData.upload();
        }
    }

    public void wipeIndices() {
        for (InstanceData value : datas.values()) {
            value.writeInstance(0);
        }
    }
}

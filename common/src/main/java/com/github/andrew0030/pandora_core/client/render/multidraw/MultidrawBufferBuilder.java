package com.github.andrew0030.pandora_core.client.render.multidraw;

import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Objects;

public class MultidrawBufferBuilder implements VertexConsumer {
    BufferBuilder direct;
    int startRange = 0;
    int vertex = 0;
    ArrayList<MeshRange> ranges = new ArrayList<>();
    ReadOnlyList<MeshRange> readOnlyList = new ReadOnlyList<>(ranges);

    public BufferBuilder getDirect() {
        return direct;
    }

    public ReadOnlyList<MeshRange> getRanges() {
        return readOnlyList;
    }

    public MultidrawBufferBuilder(BufferBuilder direct) {
        this.direct = direct;
    }

    @Override
    public VertexConsumer vertex(double v, double v1, double v2) {
        return direct.vertex(v, v1, v2);
    }

    @Override
    public VertexConsumer color(int i, int i1, int i2, int i3) {
        return direct.color(i, i1, i2, i3);
    }

    @Override
    public VertexConsumer uv(float v, float v1) {
        return direct.uv(v, v1);
    }

    @Override
    public VertexConsumer overlayCoords(int i, int i1) {
        return direct.overlayCoords(i, i1);
    }

    @Override
    public VertexConsumer uv2(int i, int i1) {
        return direct.uv2(i, i1);
    }

    @Override
    public VertexConsumer normal(float v, float v1, float v2) {
        return direct.normal(v, v1, v2);
    }

    @Override
    public void endVertex() {
        direct.endVertex();
        vertex++;
    }

    public MeshRange endMesh(String meshName) {
        MeshRange range;
        ranges.add(range = new MeshRange(
                meshName,
                startRange, vertex
        ));
        startRange = vertex;
        return range;
    }

    @Override
    public void defaultColor(int i, int i1, int i2, int i3) {
        direct.defaultColor(i, i1, i2, i3);
    }

    @Override
    public void unsetDefaultColor() {
        direct.unsetDefaultColor();
    }

    @Override
    public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
        direct.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13);
    }

    @Override
    public VertexConsumer color(float $$0, float $$1, float $$2, float $$3) {
        return direct.color($$0, $$1, $$2, $$3);
    }

    @Override
    public VertexConsumer color(int $$0) {
        return direct.color($$0);
    }

    @Override
    public VertexConsumer uv2(int $$0) {
        return direct.uv2($$0);
    }

    @Override
    public VertexConsumer overlayCoords(int $$0) {
        return direct.overlayCoords($$0);
    }

    @Override
    public void putBulkData(PoseStack.Pose $$0, BakedQuad $$1, float $$2, float $$3, float $$4, int $$5, int $$6) {
        direct.putBulkData($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    public void putBulkData(PoseStack.Pose $$0, BakedQuad $$1, float[] $$2, float $$3, float $$4, float $$5, int[] $$6, int $$7, boolean $$8) {
        direct.putBulkData($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    @Override
    public VertexConsumer vertex(Matrix4f $$0, float $$1, float $$2, float $$3) {
        return direct.vertex($$0, $$1, $$2, $$3);
    }

    @Override
    public VertexConsumer normal(Matrix3f $$0, float $$1, float $$2, float $$3) {
        return direct.normal($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultidrawBufferBuilder that = (MultidrawBufferBuilder) o;
        return vertex == that.vertex && Objects.equals(direct, that.direct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direct, vertex);
    }

    public static final class MeshRange {
        String name;
        int start, end;

        public MeshRange(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }
}

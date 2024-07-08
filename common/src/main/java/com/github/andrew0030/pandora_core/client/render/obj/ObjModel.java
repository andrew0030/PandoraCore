package com.github.andrew0030.pandora_core.client.render.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.compress.utils.IOUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.ArrayList;

@SuppressWarnings("unused")
public record ObjModel(Vector3f[] v, Vec2[] vt, Vector3f[] vn, Face[] faces) {
    public void render(PoseStack stack, VertexConsumer buffer, int packedLight) {
        try {
            for (Face face : faces) {
                Vector3f v1 = v[face.v1 - 1];
                Vector3f v2 = v[face.v2 - 1];
                Vector3f v3 = v[face.v3 - 1];

                Vec2 vt1 = vt[face.vt1 - 1];
                Vec2 vt2 = vt[face.vt2 - 1];
                Vec2 vt3 = vt[face.vt3 - 1];

                Vector3f vn1 = vn[face.vn1 - 1];
                Vector3f vn2 = vn[face.vn2 - 1];
                Vector3f vn3 = vn[face.vn3 - 1];

                addVertex(stack, buffer, v1.x(), v1.y(), v1.z(), vt1.x, 1 - vt1.y, packedLight, vn1.x(), vn1.y(), vn1.z());
                addVertex(stack, buffer, v2.x(), v2.y(), v2.z(), vt2.x, 1 - vt2.y, packedLight, vn2.x(), vn2.y(), vn2.z());
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(PoseStack stack, VertexConsumer buffer, float red, float blue, float green, int packedLight) {
        try {
            for (Face face : faces) {
                Vector3f v1 = v[face.v1 - 1];
                Vector3f v2 = v[face.v2 - 1];
                Vector3f v3 = v[face.v3 - 1];

                Vec2 vt1 = vt[face.vt1 - 1];
                Vec2 vt2 = vt[face.vt2 - 1];
                Vec2 vt3 = vt[face.vt3 - 1];

                Vector3f vn1 = vn[face.vn1 - 1];
                Vector3f vn2 = vn[face.vn2 - 1];
                Vector3f vn3 = vn[face.vn3 - 1];

                addVertex(stack, buffer, v1.x(), v1.y(), v1.z(), vt1.x, 1 - vt1.y, packedLight, vn1.x(), vn1.y(), vn1.z(), red, blue, green);
                addVertex(stack, buffer, v2.x(), v2.y(), v2.z(), vt2.x, 1 - vt2.y, packedLight, vn2.x(), vn2.y(), vn2.z(), red, blue, green);
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z(), red, blue, green);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderQuads(PoseStack stack, VertexConsumer buffer, float red, float green, float blue, int packedLight) {
        try {
            for (Face face : faces) {
                Vector3f v1 = v[face.v1 - 1];
                Vector3f v2 = v[face.v2 - 1];
                Vector3f v3 = v[face.v3 - 1];

                Vec2 vt1 = vt[face.vt1 - 1];
                Vec2 vt2 = vt[face.vt2 - 1];
                Vec2 vt3 = vt[face.vt3 - 1];

                Vector3f vn1 = vn[face.vn1 - 1];
                Vector3f vn2 = vn[face.vn2 - 1];
                Vector3f vn3 = vn[face.vn3 - 1];

                addVertex(stack, buffer, v1.x(), v1.y(), v1.z(), vt1.x, 1 - vt1.y, packedLight, vn1.x(), vn1.y(), vn1.z(), red, green, blue);
                addVertex(stack, buffer, v2.x(), v2.y(), v2.z(), vt2.x, 1 - vt2.y, packedLight, vn2.x(), vn2.y(), vn2.z(), red, green, blue);
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z(), red, green, blue);
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z(), red, green, blue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderQuads(PoseStack stack, VertexConsumer buffer, int packedLight) {
        try {
            for (Face face : faces) {
                Vector3f v1 = v[face.v1 - 1];
                Vector3f v2 = v[face.v2 - 1];
                Vector3f v3 = v[face.v3 - 1];

                Vec2 vt1 = vt[face.vt1 - 1];
                Vec2 vt2 = vt[face.vt2 - 1];
                Vec2 vt3 = vt[face.vt3 - 1];

                Vector3f vn1 = vn[face.vn1 - 1];
                Vector3f vn2 = vn[face.vn2 - 1];
                Vector3f vn3 = vn[face.vn3 - 1];

                addVertex(stack, buffer, v1.x(), v1.y(), v1.z(), vt1.x, 1 - vt1.y, packedLight, vn1.x(), vn1.y(), vn1.z());
                addVertex(stack, buffer, v2.x(), v2.y(), v2.z(), vt2.x, 1 - vt2.y, packedLight, vn2.x(), vn2.y(), vn2.z());
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z());
                addVertex(stack, buffer, v3.x(), v3.y(), v3.z(), vt3.x, 1 - vt3.y, packedLight, vn3.x(), vn3.y(), vn3.z());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVertex(PoseStack stack, VertexConsumer buffer, float x, float y, float z, float u, float v, int packedLight, float nx, float ny, float nz) {
        addVertex(stack, buffer, x, y, z, u, v, packedLight, nx, ny, nz, 1, 1, 1);
    }

    private void addVertex(PoseStack stack, VertexConsumer buffer, float x, float y, float z, float u, float v, int packedLight, float nx, float ny, float nz, float r, float g, float b) {
        pos(buffer, stack.last().pose(), x, y, z);
        buffer.color(r, g, b, 1F);
        buffer.uv(u, v);
        buffer.overlayCoords(OverlayTexture.NO_OVERLAY);
        buffer.uv2(packedLight);
        normal(buffer, stack.last().normal(), nx, ny, nz);
        buffer.endVertex();
    }

    private void pos(VertexConsumer buffer, Matrix4f matrix4f, float x, float y, float z) {
        // Calling 'buffer.vertex(matrix4f, x, y, z)' allocates a Vector4f
        // To avoid allocating so many short-lived vectors we do the transform ourselves instead
        float w = 1.0F;
        float tx = Math.fma(matrix4f.m00(), x, Math.fma(matrix4f.m10(), y, Math.fma(matrix4f.m20(), z, matrix4f.m30() * w)));
        float ty = Math.fma(matrix4f.m01(), x, Math.fma(matrix4f.m11(), y, Math.fma(matrix4f.m21(), z, matrix4f.m31() * w)));
        float tz = Math.fma(matrix4f.m02(), x, Math.fma(matrix4f.m12(), y, Math.fma(matrix4f.m22(), z, matrix4f.m32() * w)));

        buffer.vertex(tx, ty, tz);
    }

    private void normal(VertexConsumer buffer, Matrix3f matrix3f, float x, float y, float z) {
        // Calling 'bufferBuilder.normal(matrix3f, x, y, z)' allocates a Vector3f
        // To avoid allocating so many short-lived vectors we do the transform ourselves instead
        float nx = Math.fma(matrix3f.m00(), x, Math.fma(matrix3f.m10(), y, matrix3f.m20() * z));
        float ny = Math.fma(matrix3f.m01(), x, Math.fma(matrix3f.m11(), y, matrix3f.m21() * z));
        float nz = Math.fma(matrix3f.m02(), x, Math.fma(matrix3f.m12(), y, matrix3f.m22() * z));

        buffer.normal(nx, ny, nz);
    }

    public static ObjModel loadModel(InputStream stream) {
        byte[] modelData;
        try {
            modelData = stream.readAllBytes();
            IOUtils.closeQuietly(stream);
        } catch (Throwable err) {
            IOUtils.closeQuietly(stream);
            return null;
        }

        String modelString = new String(modelData);
        String[] modelLines = modelString.split("\\r?\\n");

        ArrayList<Vector3f> vList = new ArrayList<>();
        ArrayList<Vec2> vtList = new ArrayList<>();
        ArrayList<Vector3f> vnList = new ArrayList<>();
        ArrayList<Face> faceList = new ArrayList<>();

        for (String line : modelLines) {
            String[] lineSpit = line.split(" ");
            switch (lineSpit[0]) {
                case "v" ->
                        vList.add(new Vector3f(Float.parseFloat(lineSpit[1]), Float.parseFloat(lineSpit[2]), Float.parseFloat(lineSpit[3])));
                case "vt" -> vtList.add(new Vec2(Float.parseFloat(lineSpit[1]), Float.parseFloat(lineSpit[2])));
                case "vn" ->
                        vnList.add(new Vector3f(Float.parseFloat(lineSpit[1]), Float.parseFloat(lineSpit[2]), Float.parseFloat(lineSpit[3])));
                case "f" -> faceList.add(Face.construct(lineSpit[1], lineSpit[2], lineSpit[3]));
                default -> {
                }
            }
        }

        Vector3f[] vArray = vList.toArray(new Vector3f[0]);
        Vec2[] vtArray = vtList.toArray(new Vec2[0]);
        Vector3f[] vnArray = vnList.toArray(new Vector3f[0]);
        Face[] faces = faceList.toArray(new Face[0]);

        return new ObjModel(vArray, vtArray, vnArray, faces);
    }

    private record Face(
            // coords
            int v1, int vt1, int vn1,
            // uvs
            int v2, int vt2, int vn2,
            // normals
            int v3, int vt3, int vn3
    ) {
        // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
        public static Face construct(String v1, String v2, String v3) {
            String[] s1 = v1.split("/");
            String[] s2 = v2.split("/");
            String[] s3 = v3.split("/");

            return new Face(
                    // vert 1
                    Integer.parseInt(s1[0]), // coord
                    Integer.parseInt(s1[1]), // tex
                    Integer.parseInt(s1[2]), // norm
                    // vert 2
                    Integer.parseInt(s2[0]), // coord
                    Integer.parseInt(s2[1]), // tex
                    Integer.parseInt(s2[2]), // norm
                    // vert 3
                    Integer.parseInt(s3[0]), // coord
                    Integer.parseInt(s3[1]), // tex
                    Integer.parseInt(s3[2])  // norm
            );
        }
    }
}
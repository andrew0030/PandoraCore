package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.render.instancing.InstanceData;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceDataElement;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.instancing.InstancedVBO;
import com.github.andrew0030.pandora_core.client.render.multidraw.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.multidraw.MultidrawBufferBuilder;
import com.github.andrew0030.pandora_core.client.render.obj.ObjModel;
import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TemplateShaderTest {
    public static final InstanceDataElement POSITION = new InstanceDataElement("paco_Inject_Translation", NumericPrimitive.FLOAT, 3);
    public static final InstanceFormat FORMAT = new InstanceFormat(
            POSITION
    );
    private static final int CUBE_COUNT = 1_000_000;
    protected static final InstanceData data = new InstanceData(FORMAT, CUBE_COUNT, VertexBuffer.Usage.STATIC);
    protected static final CollectiveVBO instancedVBO = new CollectiveVBO(VertexBuffer.Usage.STATIC, FORMAT);
    protected static final BufferBuilder builder = new BufferBuilder(3497);

    public static void uploadVBO(ObjModel queenObj, ObjModel cubeObj) {
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        MultidrawBufferBuilder multidrawBuffer = new MultidrawBufferBuilder(builder);
//        queenObj.render(
//                new PoseStack(),
//                multidrawBuffer, LightTexture.FULL_SKY
//        );
//        MultidrawBufferBuilder.MeshRange queenRange = multidrawBuffer.endMesh("queen");
        cubeObj.render(
                new PoseStack(),
                multidrawBuffer, LightTexture.FULL_SKY
        );
        MultidrawBufferBuilder.MeshRange cubeRange = multidrawBuffer.endMesh("cube");

        instancedVBO.bind();
        MultidrawBufferBuilder.MeshRange[] ranges = new MultidrawBufferBuilder.MeshRange[CUBE_COUNT];
        {
            data.writeInstance(0);
            int rem = CUBE_COUNT;
            boolean cube = false;
            int cbrt = (int) Math.pow(CUBE_COUNT, 1 / (cube ? 3d : 2d)) - 1;
            int idx = 0;
            for (int x = 0; x < cbrt; x++) {
                for (int z = 0; z < cbrt; z++) {
                    if (cube) {
                        for (int y = 0; y < cbrt; y++) {
                            data.writeFloat(x);
                            data.writeFloat(y);
                            data.writeFloat(z);
                            data.finishInstance();
                        }
                    } else {
                        data.writeFloat(x * 4);
                        data.writeFloat(0);
                        data.writeFloat(z * 4);
                        data.finishInstance();
                    }
                    ranges[idx] = Math.random() > 0.5 ? cubeRange : cubeRange;
                    idx++;
                    rem--;
                }
            }
            int i = 0;
            while (idx != CUBE_COUNT) {
                data.writeFloat(0);
                if (cube) data.writeFloat((rem + cbrt + 1) * 4);
                else data.writeFloat((i + 1) * 4);
                data.writeFloat(0);
                data.finishInstance();
                rem--;
                ranges[idx] = Math.random() > 0.5 ? cubeRange : cubeRange;
                idx++;
                i++;
            }
        }

        instancedVBO.defineRanges(ranges);
        instancedVBO.setDrawCount(data.drawCount());
        data.upload();
        instancedVBO.upload(builder.end());
        instancedVBO.bindData(data);
        builder.clear();
        VertexBuffer.unbind();
    }

    public static void draw(PoseStack stack, double x, double y, double z) {
        RenderType type = PaCoRenderTypes.type;

        RenderSystem.setShaderTexture(0, new ResourceLocation(
                "minecraft:dynamic/light_map_1"
        ));
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
        RenderSystem.getModelViewStack().translate(
                -x,
                -y,
                -z
        );
        RenderSystem.getModelViewStack().scale(0.125f, 0.125f, 0.125f);
        RenderSystem.getModelViewStack().scale(0.125f, 0.125f, 0.125f);
        RenderSystem.getModelViewStack().translate(1f, 1f, 1f);
        RenderSystem.applyModelViewMatrix();

        try {
            type.setupRenderState();
            // TODO: figure out how to not crash with iris
            instancedVBO.bind();
            instancedVBO.drawWithShader(
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    RenderSystem.getShader()
            );
            VertexBuffer.unbind();
            type.clearRenderState();
        } catch (Throwable err) {
        }
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }
}

package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveBufferBuilder;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceDataElement;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.obj.ObjModel;
import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TemplateShaderTest {
    public static final InstanceDataElement POSITION = new InstanceDataElement("paco_Inject_Translation", NumericPrimitive.FLOAT, 3);
    public static final InstanceFormat FORMAT = new InstanceFormat(
            POSITION
    );
    private static final int CUBE_COUNT = 100_000;
    protected static final CollectiveDrawData data = new CollectiveDrawData(FORMAT, CUBE_COUNT, VertexBuffer.Usage.STATIC);
    public static final CollectiveVBO collectiveVBO = new CollectiveVBO(VertexBuffer.Usage.STATIC, FORMAT);
    protected static final BufferBuilder builder = new BufferBuilder(3497);
    public static CollectiveBufferBuilder.MeshRange cubeRange;
    public static CollectiveBufferBuilder.MeshRange queenRange;

    public static void uploadVBO(ObjModel queenObj, ObjModel cubeObj) {
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        CollectiveBufferBuilder multidrawBuffer = new CollectiveBufferBuilder(builder);
        queenObj.render(
                new PoseStack(),
                multidrawBuffer, LightTexture.FULL_SKY
        );
        queenRange = multidrawBuffer.endMesh("queen");
        cubeObj.render(
                new PoseStack(),
                multidrawBuffer, LightTexture.FULL_SKY
        );
        cubeRange = multidrawBuffer.endMesh("cube");

        collectiveVBO.bind();
        {
            int rem = CUBE_COUNT;
            boolean cube = false;
            int cbrt = (int) Math.pow(CUBE_COUNT, 1 / (cube ? 3d : 2d)) - 1;
            int idx = 0;

            data.writeMesh(queenRange);
            data.writeInstance(0);
            data.activateData();
            data.writeMesh(cubeRange);
            data.writeInstance(0);
            data.activateData();

            for (int x = 0; x < cbrt; x++) {
                for (int z = 0; z < cbrt; z++) {
                    data.writeMesh(Math.random() > 0.5 ? queenRange : cubeRange);
                    if (cube) {
                        for (int y = 0; y < cbrt; y++) {
                            data
                                    .writeFloat(x, y, z)
                                    .finishInstance();
                        }
                    } else {
                        data
                                .writeFloat(x * 4, 0, z * 4)
                                .finishInstance();
                    }
                    idx++;
                    rem--;
                }
            }
            int i = 0;
            while (idx != CUBE_COUNT) {
                data.writeMesh(Math.random() > 0.5 ? queenRange : cubeRange);
                data.writeFloat(0);
                if (cube) data.writeFloat((rem + cbrt + 1) * 4);
                else data.writeFloat((i + 1) * 4);
                data.writeFloat(0);
                data.finishInstance();
                rem--;
                idx++;
                i++;
            }
        }

        collectiveVBO.setupData(data);
        data.upload();
        collectiveVBO.upload(builder.end());
        builder.clear();
        VertexBuffer.unbind();
    }

    public static void draw(PoseStack stack, double x, double y, double z) {

        // Disables the debug rendering, because my PC sounds like a plane engine every time ~ andrew
        if(true) return;

        RenderType type = PaCoRenderTypes.type;

        RenderSystem.setShaderFogShape(FogShape.SPHERE);
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
            collectiveVBO.bind();
            collectiveVBO.drawWithShader(
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
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
    }
}

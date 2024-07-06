package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.render.instancing.InstanceData;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceDataElement;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.instancing.InstancedVBO;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.utils.shader.PaCoShaderStateShard;
import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class TemplateShaderTest {
    private static final TemplatedShaderInstance shader = TemplateManager.getTemplated(new ResourceLocation("pandora_core:shaders/paco/templated/entity_instanced.glsl"));

    public static final PaCoShaderStateShard shaderStateShard = new PaCoShaderStateShard(shader);

    public static final RenderType type = RenderType.create(
            "pandora_core:test",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            DefaultVertexFormat.NEW_ENTITY.getVertexSize() * 64,
            true, false,
            RenderType.CompositeState.builder()
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setShaderState(shaderStateShard)
                    .createCompositeState(true)
    );

    protected static void vert(
            VertexConsumer consumer,
            double x, double y, double z,
            double u, double v,
            double nx, double ny, double nz
    ) {
        consumer.vertex((float) x, (float) y, (float) z)
                .color(255, 255, 255, 255)
                .uv((float) u, (float) v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal((float) nx, (float) ny, (float) nz)
                .endVertex();
    }

    public static final InstanceDataElement POSITION = new InstanceDataElement("paco_Inject_Translation", NumericPrimitive.FLOAT, 3);
    public static final InstanceFormat FORMAT = new InstanceFormat(
            POSITION
    );
    protected static final InstanceData data = new InstanceData(FORMAT, 10000);
    protected static final InstancedVBO instancedVBO = new InstancedVBO(VertexBuffer.Usage.STATIC, FORMAT);
    protected static final BufferBuilder builder = new BufferBuilder(3497);

    static {
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        VertexConsumer consumer = builder;
        // +x
        {
            vert(consumer,
                    1, 0, 0,
                    0, 1,
                    0, 0, -1);
            vert(consumer,
                    1, 1, 0,
                    1, 1,
                    0, 0, -1);
            vert(consumer,
                    1, 1, 1,
                    1, 0,
                    0, 0, -1);
            vert(consumer,
                    1, 0, 1,
                    0, 0,
                    0, 0, -1);
        }
        // -x
        {
            vert(consumer,
                    0, 0, 1,
                    1, 1,
                    0, 0, -1);
            vert(consumer,
                    0, 1, 1,
                    0, 1,
                    0, 0, -1);
            vert(consumer,
                    0, 1, 0,
                    0, 0,
                    0, 0, -1);
            vert(consumer,
                    0, 0, 0,
                    1, 0,
                    0, 0, -1);
        }
        // -z
        {
            vert(consumer,
                    0, 0, 0,
                    1, 0,
                    -1, 0, 0);
            vert(consumer,
                    0, 1, 0,
                    0, 0,
                    -1, 0, 0);
            vert(consumer,
                    1, 1, 0,
                    0, 1,
                    -1, 0, 0);
            vert(consumer,
                    1, 0, 0,
                    1, 1,
                    -1, 0, 0);
        }
        // +z
        {
            vert(consumer,
                    1, 0, 1,
                    0, 0,
                    -1, 0, 0);
            vert(consumer,
                    1, 1, 1,
                    1, 0,
                    -1, 0, 0);
            vert(consumer,
                    0, 1, 1,
                    1, 1,
                    -1, 0, 0);
            vert(consumer,
                    0, 0, 1,
                    0, 1,
                    -1, 0, 0);
        }
        // +y
        {
            vert(consumer,
                    0, 1, 0,
                    0, 0,
                    0, 1, 0);
            vert(consumer,
                    0, 1, 1,
                    1, 0,
                    0, 1, 0);
            vert(consumer,
                    1, 1, 1,
                    1, 1,
                    0, 1, 0);
            vert(consumer,
                    1, 1, 0,
                    0, 1,
                    0, 1, 0);
        }
        // -y
        {
            vert(consumer,
                    1, 0, 0,
                    1, 0,
                    0, -1, 0);
            vert(consumer,
                    1, 0, 1,
                    0, 0,
                    0, -1, 0);
            vert(consumer,
                    0, 0, 1,
                    0, 1,
                    0, -1, 0);
            vert(consumer,
                    0, 0, 0,
                    1, 1,
                    0, -1, 0);
        }

        instancedVBO.bind();
        {
            data.writeInstance(0);
            for (int i = 0; i < 10000; i++) {
                data.writeFloat(0);
                data.writeFloat(i);
                data.writeFloat(0);
            }
        }

        instancedVBO.uploadInstanceData(data);
        instancedVBO.upload(builder.end());
        builder.clear();
        VertexBuffer.unbind();
    }

    public static void draw(PoseStack stack, double x, double y, double z) {
//        if (true) return;

        RenderType type = RenderType.create(
                "pandora_core:test",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.NEW_ENTITY.getVertexSize() * 64,
                true, false,
                RenderType.CompositeState.builder()
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setShaderState(shaderStateShard)
                        .createCompositeState(true)
        );

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
        RenderSystem.applyModelViewMatrix();

        try {
            type.setupRenderState();
            // TODO: figure out how to not crash with iris
            instancedVBO.bind();
            data.writeInstance(1000);
            instancedVBO.uploadInstanceData(data);
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

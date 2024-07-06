package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.utils.shader.PaCoShaderStateShard;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

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
            PoseStack stk,
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

        Tesselator t = Tesselator.getInstance();
        t.getBuilder().begin(type.mode(), type.format());
        VertexConsumer consumer = t.getBuilder();

        RenderSystem.setShaderTexture(0, new ResourceLocation(
                "minecraft:dynamic/light_map_1"
        ));
        stack.pushPose();
        stack.translate(
                -x,
                -y,
                -z
        );
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
        RenderSystem.applyModelViewMatrix();
        RenderSystem.getModelViewStack().popPose();
        stack.popPose();
        // +x
        {
            vert(stack, consumer,
                    1, 0, 0,
                    0, 1,
                    1, 0, 0);
            vert(stack, consumer,
                    1, 1, 0,
                    1, 1,
                    1, 0, 0);
            vert(stack, consumer,
                    1, 1, 1,
                    1, 0,
                    1, 0, 0);
            vert(stack, consumer,
                    1, 0, 1,
                    0, 0,
                    1, 0, 0);
        }
        // -x
        {
            vert(stack, consumer,
                    0, 0, 1,
                    1, 1,
                    -1, 0, 0);
            vert(stack, consumer,
                    0, 1, 1,
                    0, 1,
                    -1, 0, 0);
            vert(stack, consumer,
                    0, 1, 0,
                    0, 0,
                    -1, 0, 0);
            vert(stack, consumer,
                    0, 0, 0,
                    1, 0,
                    -1, 0, 0);
        }
        // -z
        {
            vert(stack, consumer,
                    0, 0, 0,
                    1, 0,
                    0, 0, 1);
            vert(stack, consumer,
                    0, 1, 0,
                    0, 0,
                    0, 0, 1);
            vert(stack, consumer,
                    1, 1, 0,
                    0, 1,
                    0, 0, 1);
            vert(stack, consumer,
                    1, 0, 0,
                    1, 1,
                    0, 0, 1);
        }
        // +z
        {
            vert(stack, consumer,
                    1, 0, 1,
                    0, 0,
                    0, 0, 1);
            vert(stack, consumer,
                    1, 1, 1,
                    1, 0,
                    0, 0, 1);
            vert(stack, consumer,
                    0, 1, 1,
                    1, 1,
                    0, 0, 1);
            vert(stack, consumer,
                    0, 0, 1,
                    0, 1,
                    0, 0, 1);
        }
        // +y
        {
            vert(stack, consumer,
                    0, 1, 0,
                    0, 0,
                    0, 1, 0);
            vert(stack, consumer,
                    0, 1, 1,
                    1, 0,
                    0, 1, 0);
            vert(stack, consumer,
                    1, 1, 1,
                    1, 1,
                    0, 1, 0);
            vert(stack, consumer,
                    1, 1, 0,
                    0, 1,
                    0, 1, 0);
        }
        // -y
        {
            vert(stack, consumer,
                    1, 0, 0,
                    1, 0,
                    0, -1, 0);
            vert(stack, consumer,
                    1, 0, 1,
                    0, 0,
                    0, -1, 0);
            vert(stack, consumer,
                    0, 0, 1,
                    0, 1,
                    0, -1, 0);
            vert(stack, consumer,
                    0, 0, 0,
                    1, 1,
                    0, -1, 0);
        }
        type.setupRenderState();
        try {
            // TODO: figure out how to not crash with iris
            t.end();
        } catch (Throwable err) {
        }
        type.clearRenderState();
        RenderSystem.applyModelViewMatrix();
    }
}

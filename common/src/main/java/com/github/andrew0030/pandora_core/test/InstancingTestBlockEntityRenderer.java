package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.block_entities.InstancingTestBlockEntity;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceDataElement;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.utils.enums.NumericPrimitive;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class InstancingTestBlockEntityRenderer extends InstancedBlockEntityRenderer<InstancingTestBlockEntity> {
    public static final InstanceDataElement POSITION = new InstanceDataElement("paco_Inject_Translation", NumericPrimitive.FLOAT, 3);
    public static final InstanceFormat FORMAT = new InstanceFormat(
            POSITION
    );

    public InstancingTestBlockEntityRenderer() {
        this(
                FORMAT,
                TemplateShaderTest.collectiveVBO
        );
    }

    public InstancingTestBlockEntityRenderer(InstanceFormat format, CollectiveVBO vbo) {
        super(format, vbo);
    }

    @Override
    public void render(Level level, InstancingTestBlockEntity object, BlockPos pos, CollectiveDrawData data) {
        data.writeMesh(TemplateShaderTest.queenRange);
        data.ensureInstance();
        data.activateData();
        data.writeFloat(pos.getX() + 0.5f);
        data.writeFloat(pos.getY());
        data.writeFloat(pos.getZ() + 0.5f);
        data.finishInstance();
    }

    @Override
    public void flush(Level level, CollectiveDrawData data) {
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
        RenderSystem.setShaderTexture(0, new ResourceLocation(
                "minecraft:dynamic/light_map_1"
        ));
        RenderType type = PaCoRenderTypes.type;
        type.setupRenderState();
        vbo.setupData(data);
        RenderSystem.getShader().apply();
        data.upload();
        vbo.bind();
        vbo.drawWithShader(
                RenderSystem.getModelViewMatrix(),
                RenderSystem.getProjectionMatrix(),
                RenderSystem.getShader()
        );
        type.clearRenderState();
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
    }
}
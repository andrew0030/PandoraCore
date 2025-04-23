package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.client.render.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.test.PaCoRenderTypes;
import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.joml.Matrix3f;
import org.joml.Random;

public class SBERenderer extends InstancedBlockEntityRenderer<PandoraCoreForge.SBE> {
    public SBERenderer() {
        this(
                TemplateShaderTest.FORMAT,
                TemplateShaderTest.collectiveVBO
        );
    }

    public SBERenderer(InstanceFormat format, CollectiveVBO vbo) {
        super(format, vbo);
    }

    @Override
    public void render(Level level, PandoraCoreForge.SBE object, BlockPos pos, CollectiveDrawData data) {
        Matrix3f matrix3f = new Matrix3f();
        XoroshiroRandomSource source = new XoroshiroRandomSource(
                new ChunkPos(pos).toLong(),
                pos.asLong()
        );
        matrix3f = matrix3f.rotate(
//                source.nextFloat() * 90,
                new Random().nextFloat() * (float) onMath.PI * 2,
                1, 0, 0
        );
        data.writeMesh(TemplateShaderTest.queenRange);
        data.ensureInstance();
        data.activateData();
        data.writeFloat(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
        data.writeMatrix(matrix3f);
//        data.writeFloat(1, 0, 0);
//        data.writeFloat(0, 1, 0);
//        data.writeFloat(0, 0, 1);
        data.finishInstance();
    }

    @Override
    public void flush(Level level, CollectiveDrawData data) {
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
        RenderSystem.setShaderTexture(0, new ResourceLocation(
//                "minecraft:dynamic/light_map_1"
                "minecraft:textures/block/white_concrete.png"
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

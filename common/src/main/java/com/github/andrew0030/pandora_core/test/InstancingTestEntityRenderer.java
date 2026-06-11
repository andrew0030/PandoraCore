package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.block_entities.InstancingTestBlockEntity;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchKey;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoShaderStateShard;
import com.github.andrew0030.pandora_core.test.entity.TestEntity;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Random;

public class InstancingTestEntityRenderer extends InstancedEntityRenderer<TestEntity> {
    public InstancingTestEntityRenderer() {
        this(TemplateShaderTest.FORMAT);
    }

    private final BatchKey STANDARD_KEY = new BatchKey() {
        public void flush(CollectiveDrawData data) {
            vbo.setupData(data, PaCoRenderTypes.shader);
            data.upload();
	        vbo.drawWithShader(
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    RenderSystem.getShader()
            );
        }
    };

	CollectiveVBO vbo;
	
    public InstancingTestEntityRenderer(InstanceFormat format) {
        super(format);
	    this.vbo = TemplateShaderTest.collectiveVBO;
    }

    @Override
    public void render(PacoInstancingLevel ilevel, TestEntity object, Vec3 pos, BatchData batches, float pct, Vec3 cameraPos) {
	    Level level = (Level) ilevel;
		
        CollectiveDrawData data = batches.buildBatch(STANDARD_KEY);

        Matrix3f matrix3f = new Matrix3f();
        matrix3f = matrix3f.rotate(
//                source.nextFloat() * 90,
                new Random().nextFloat() * (float) Math.PI * 2,
                1, 0, 0
        ).identity();
        data.writeMesh(TemplateShaderTest.queenRange);
        data.ensureInstance();
        data.activateData();
	    data.writeFloat((float) (pos.x - cameraPos.x), (float) (pos.y - cameraPos.y), (float) (pos.z - cameraPos.z));
        data.writeMatrix(matrix3f);
	    Vec3 probe = object.getLightProbePosition(pct);
	    BlockPos bp = (BlockPos.containing(probe));
	    int $$0 = LightTexture.pack(
//                2, 0
                level.getBrightness(LightLayer.BLOCK, bp),
                level.getBrightness(LightLayer.SKY, bp)
        );
        data.writeInt($$0);

        data.finishInstance();
    }

    @Override
    public void flush(PacoInstancingLevel ilevel, BatchData data) {
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
	    RenderType type = PaCoRenderTypes.type;
	    type.setupRenderState();
	    PaCoShaderStateShard shaderShard = PaCoRenderTypes.shaderStateShard;
	    RenderSystem.setShaderTexture(0, new ResourceLocation(
//                "minecraft:dynamic/light_map_1"
			    "minecraft:textures/block/white_concrete.png"
	    ));
	    if (shaderShard.shouldRender()) {
		    RenderSystem.getShader().apply();
		    vbo.bind();
		    data.flush();
		    vbo.unbindVBO();
	    }
        type.clearRenderState();
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
    }
}
package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.block_entities.InstancingTestBlockEntity;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchKey;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoShaderStateShard;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
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

public class InstancingTestBlockEntityRenderer extends InstancedBlockEntityRenderer<InstancingTestBlockEntity> {
	public InstancingTestBlockEntityRenderer() {
		this(
				TemplateShaderTest.FORMAT,
				TemplateShaderTest.collectiveVBO
		);
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
	
	public InstancingTestBlockEntityRenderer(InstanceFormat format, CollectiveVBO vbo) {
		super(format);
		this.vbo = vbo;
	}
	
	@Override
	public void render(PacoInstancingLevel ilevel, InstancingTestBlockEntity object, BlockPos pos, BatchData batches, float pct, Vec3 cameraPos) {
		Level level = (Level) ilevel;
		
		CollectiveDrawData data = batches.buildBatch(STANDARD_KEY);
		
		Matrix3f matrix3f = new Matrix3f();
		XoroshiroRandomSource source = new XoroshiroRandomSource(
				new ChunkPos(pos).toLong(),
				pos.asLong()
		);
		matrix3f = matrix3f.rotate(
//                source.nextFloat() * 90,
				new Random().nextFloat() * (float) Math.PI * 2,
				1, 0, 0
		).identity();
		data.writeMesh(TemplateShaderTest.queenRange);
		data.ensureInstance();
		data.activateData();
		data.writeFloat((float) (pos.getX() - cameraPos.x) + 0.5f, (float) (pos.getY() - cameraPos.y), (float) (pos.getZ() - cameraPos.z) + 0.5f);
		data.writeMatrix(matrix3f);
		int $$0 = LightTexture.pack(
//                2, 0
				level.getBrightness(LightLayer.BLOCK, pos),
				level.getBrightness(LightLayer.SKY, pos)
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
package com.github.andrew0030.pandora_core.test;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin.ItemDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchKey;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstanceRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoShaderStateShard;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class InstancingTestItemRenderer extends InstancedItemRenderer {
	CollectiveVBO vbo;
	
	public InstancingTestItemRenderer() {
		super(TemplateShaderTest.FORMAT_MAT4);
		this.vbo = TemplateShaderTest.collectiveVBO;
	}
	
	private final BatchKey STANDARD_KEY = new BatchKey() {
		public void flush(CollectiveDrawData data) {
			vbo.setupData(data, PaCoRenderTypes.shaderItem);
			data.upload();
			vbo.drawWithShader(
					RenderSystem.getModelViewMatrix(),
					RenderSystem.getProjectionMatrix(),
					RenderSystem.getShader()
			);
		}
	};
	
	@Override
	public void render(
			InstancingEnvironment env, ItemStack object,
			ItemDrawData drawData, BatchData batches,
			float pct, Vec3 cameraPos
	) {
		CollectiveDrawData data = batches.buildBatch(STANDARD_KEY);
		
		data.writeMesh(TemplateShaderTest.queenRange);
		data.ensureInstance();
		data.activateData();
		PoseStack stk = drawData.getPoseStack();
//		stk.scale(1 / 4f, 1 / 4f, 1 / 4f);
//		stk.translate(0, -0.5f, 0);
		stk.translate(0.5f, 0, 0.5f);
		data.writeMatrix(stk.last().pose());
		int $$0 = drawData.getLightmap();
		data.writeInt($$0);
		
		data.finishInstance();
	}
	
	@Override
	public void flush(InstancingEnvironment env, BatchData data) {
		RenderSystem.setShaderFogShape(FogShape.SPHERE);
		RenderType type = PaCoRenderTypes.typeItem;
		type.setupRenderState();
		PaCoShaderStateShard shaderShard = PaCoRenderTypes.itemShaderStateShard;
		RenderSystem.setShaderTexture(0, new ResourceLocation(
				"minecraft:textures/block/white_concrete.png"
		));
		if (shaderShard.shouldRender()) {
			RenderSystem.getShader().apply();
			vbo.overrideFormat(TemplateShaderTest.FORMAT_MAT4);
			vbo.bind();
			data.flush();
			vbo.unbindVBO();
			vbo.overrideFormat(null);
		}
		type.clearRenderState();
		RenderSystem.setShaderFogShape(FogShape.CYLINDER);
	}
}

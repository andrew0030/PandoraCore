package com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchKey;
import com.github.andrew0030.pandora_core.modules.instancer.itf.ItemRendererAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoShaderStateShard;
import com.github.andrew0030.pandora_core.test.PaCoRenderTypes;
import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Random;

public class ItemEntityInstancer extends InstancedEntityRenderer<ItemEntity> {
	private final RandomSource random = RandomSource.create();
	ItemRenderer itemRenderer;
	
	CollectiveVBO vbo;
	
	public ItemEntityInstancer(InstanceFormat format) {
		super(format);
		itemRenderer = Minecraft.getInstance().getItemRenderer();
		this.vbo = TemplateShaderTest.collectiveVBO;
	}
	
	private int getRenderAmount(ItemStack stack) {
		int i = 1;
		if (stack.getCount() > 48) {
			i = 5;
		} else if (stack.getCount() > 32) {
			i = 4;
		} else if (stack.getCount() > 16) {
			i = 3;
		} else if (stack.getCount() > 1) {
			i = 2;
		}
		
		return i;
	}
	
	InstancedItemRenderer renderer;
	
	@Override
	public void render(Level level, ItemEntity object, Vec3 pos, BatchData data, float pct, Vec3 cameraPos) {
		ItemStack stk = object.getItem();
		Item item = stk.getItem();
		InstancedItemRenderer renderer = ((ItemAttachments) item).pandoraCore$getInstancedRenderer();
		if (renderer == null) return; // not instancing this
		
		int drawCount;
		RandomSource random;
		
		try {
			EntityRenderer<ItemEntity> er = (EntityRenderer<ItemEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(object);
			ItemEntityRenderer ier = (ItemEntityRenderer) er;
			drawCount = ((ItemRendererAccessor) ier).pandoraCore$getRenderAmount(stk);
			random = ((ItemRendererAccessor) ier).pandoraCore$getRandom();
		} catch (Throwable ignored) {
			drawCount = getRenderAmount(stk);
			random = this.random;
		}
		
		PoseStack pose = new PoseStack();
		
		pose.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
		
		int seed = stk.isEmpty() ? 187 : Item.getId(stk.getItem()) + stk.getDamageValue();
		random.setSeed(seed);
		
		BakedModel bakedmodel = this.itemRenderer.getModel(stk, object.level(), (LivingEntity) null, object.getId());
		boolean flag = bakedmodel.isGui3d();
		
		// setup transformation
		float yBob = Mth.sin(((float) object.getAge() + pct) / 10.0F + object.bobOffs) * 0.1F + 0.1F;
		float yScale = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
		pose.translate(0.0F, yBob + 0.25F * yScale, 0.0F);
		
		float spin = object.getSpin(pct);
		pose.mulPose(Axis.YP.rotation(spin));
		
		float scaleX = bakedmodel.getTransforms().ground.scale.x();
		float scaleY = bakedmodel.getTransforms().ground.scale.y();
		float scaleZ = bakedmodel.getTransforms().ground.scale.z();
		if (!flag) {
			float translateX = -0.0F * (float) (drawCount - 1) * 0.5F * scaleX;
			float translateY = -0.0F * (float) (drawCount - 1) * 0.5F * scaleY;
			float translateZ = -0.09375F * (float) (drawCount - 1) * 0.5F * scaleZ;
			pose.translate(translateX, translateY, translateZ);
		}
		
		ItemDrawData drawData = new ItemDrawData();
		if (level != null) {
			BlockPos bp = BlockPos.containing(object.getLightProbePosition(pct));
			drawData.lightMap = LightTexture.pack(
					level.getBrightness(LightLayer.BLOCK, bp),
					level.getBrightness(LightLayer.SKY, bp)
			);
		} else {
			drawData.lightMap = LightTexture.FULL_BRIGHT;
		}
		
		for (int k = 0; k < drawCount; ++k) {
			pose.pushPose();
			
			if (k > 0) {
				if (flag) {
					float xOff = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float yOff = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float zOff = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					pose.translate(xOff, yOff, zOff);
				} else {
					float xOff = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float yOff = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					pose.translate(xOff, yOff, 0.0F);
				}
			}
			
			pose.scale(scaleX, scaleY, scaleZ);
//			writeInstance(
//					level,
//					data,
//					pose.last().pose(),
//					object.getLightProbePosition(pct)
//			);
			drawData.matr = pose;
			this.renderer = renderer;
			renderer.render(
					level,
					stk,
					drawData,
					data,
					pct,
					cameraPos
			);
			
			pose.popPose();
			
			if (!flag)
				pose.translate(0.0F * scaleX, 0.0F * scaleY, 0.09375F * scaleZ);
		}
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
	
	private void writeInstance(
			Level level,
			BatchData batches,
			Matrix4f matr, Vec3 probe
	) {
//		CollectiveDrawData data = batches.buildBatch(STANDARD_KEY);
//
//		data.writeMesh(TemplateShaderTest.queenRange);
//		data.ensureInstance();
//		data.activateData();
//		data.writeMatrix(matr);
//		BlockPos bp = BlockPos.containing(probe);
//		int $$0 = LightTexture.pack(
//				level.getBrightness(LightLayer.BLOCK, bp),
//				level.getBrightness(LightLayer.SKY, bp)
//		);
//		data.writeInt($$0);
//
//		data.finishInstance();
	}
	
	@Override
	public void flush(Level level, BatchData data) {
//		RenderSystem.setShaderFogShape(FogShape.SPHERE);
//		RenderType type = PaCoRenderTypes.typeItem;
//		type.setupRenderState();
//		PaCoShaderStateShard shaderShard = PaCoRenderTypes.itemShaderStateShard;
//		RenderSystem.setShaderTexture(0, new ResourceLocation(
//				"minecraft:textures/block/white_concrete.png"
//		));
//		if (shaderShard.shouldRender()) {
//			RenderSystem.getShader().apply();
//			vbo.overrideFormat(TemplateShaderTest.FORMAT_MAT4);
//			vbo.bind();
		data.flush();
//			vbo.unbindVBO();
//			vbo.overrideFormat(null);
//		}
//		type.clearRenderState();
//		RenderSystem.setShaderFogShape(FogShape.CYLINDER);
	}
	
	@Override
	public CollectiveDrawData makeData() {
		return renderer.makeData();
	}
}

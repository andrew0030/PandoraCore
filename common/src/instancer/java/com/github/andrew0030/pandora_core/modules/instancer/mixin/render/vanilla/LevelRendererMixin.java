package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	@Final
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
	
	@Shadow
	@Nullable
	private ClientLevel level;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderedEntities:I", ordinal = 0), method = "renderLevel")
	public void preRenderEnts(PoseStack stack, float pct, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		PaCoRenderState.setupWorld();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
		RenderSystem.applyModelViewMatrix();
		
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		// TODO: optimize this loop
		for (Entity entity : this.level.entitiesForRendering()) {
			EntityTypeAttachments attachments = ((EntityTypeAttachments) entity.getType());
			InstancedEntityRenderer renderer = attachments.pandoraCore$getInstancedRenderer();
			if (renderer != null) {
				if (renderer.shouldRender(
						entity, camera.getPosition()
				)) {
					renderer.render((PacoInstancingLevel) level, entity, entity.getPosition(pct), pct, camera.getPosition());
				}
			}
		}
		manager.drawFrame((PacoInstancingLevel) level);
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();

//		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = (PacoInstancingLevel) level;
		manager.markFrame();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V", ordinal = 0, shift = At.Shift.AFTER), method = "renderLevel")
	public void postRenderEnts(PoseStack stack, float pct, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.drawFrame((PacoInstancingLevel) level);
		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = null;
	}
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunksInFrustum:Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", ordinal = 0), method = "renderLevel")
	public void preRenderBlockEnts(PoseStack stack, float pct, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		PaCoRenderState.setupWorld();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
//		RenderSystem.getModelViewStack().translate(
//				-$$4.getPosition().x,
//				-$$4.getPosition().y,
//				-$$4.getPosition().z
//		);
		RenderSystem.applyModelViewMatrix();
		
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		for (LevelRenderer.RenderChunkInfo info : this.renderChunksInFrustum) {
			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
			for (BlockEntity be : ((InstancingResults) chnk).getAll()) {
				InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
				if (renderer.shouldRender(
						be, camera.getPosition()
				)) {
					renderer.render((PacoInstancingLevel) level, be, be.getBlockPos(), pct, camera.getPosition());
				}
			}
		}
		manager.drawFrame((PacoInstancingLevel) level);
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		PaCoRenderState.resetInstancerState();
	}
}

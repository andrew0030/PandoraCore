package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import com.github.andrew0030.pandora_core.modules.instancer.compat.InstancerHooks;
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
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow @Final private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
	@Shadow @Nullable private ClientLevel level;
	
	@Shadow
	private @org.jetbrains.annotations.Nullable Frustum capturedFrustum;
	
	@Shadow
	private Frustum cullingFrustum;
	
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
		
		InstancerHooks.preStartInstancing();
		
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
		
		InstancerHooks.postEndInstancing();
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();

//		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = (PacoInstancingLevel) level;
		InstancerHooks.preStartInstancing();
		manager.markFrame();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V", ordinal = 0, shift = At.Shift.AFTER), method = "renderLevel")
	public void postRenderEnts(PoseStack stack, float pct, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.drawFrame((PacoInstancingLevel) level);
		InstancerHooks.postEndInstancing();
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
		RenderSystem.applyModelViewMatrix();
		
		InstancerHooks.preStartInstancing();
		
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		PacoInstancingLevel instLvl = (PacoInstancingLevel) level;
		Frustum frustum = capturedFrustum == null ? cullingFrustum : capturedFrustum;
		PaCoFrustum pcFrustum = (PaCoFrustum) frustum;
		
		CullBox box = new CullBox(0, 0, 0, 0, 0, 0);
		CullSphere sphere = new CullSphere(0, 0, 0, 0);
		
		for (LevelRenderer.RenderChunkInfo info : this.renderChunksInFrustum) {
			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
			List<BlockEntity> bes = ((InstancingResults) chnk).getAll();
			if (bes.isEmpty()) continue; // no work to do
			
			if (bes.size() < 4 ||
					pcFrustum.containsAllCorners(box.set(info.chunk.getBoundingBox()))
			) {
				for (BlockEntity be : bes) {
					InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
					
					if (renderer.shouldRender(
							be, camera.getPosition()
					)) {
						BlockPos pos = be.getBlockPos();
						renderer.render(instLvl, be, pos, pct, camera.getPosition());
					}
				}
			} else {
				for (BlockEntity be : bes) {
					InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
					
					if (renderer.shouldRender(
							be, camera.getPosition()
					)) {
						BlockPos pos = be.getBlockPos();
						renderer.getCullBox(box, instLvl, be, pos);
						sphere.contain(box);
						
						if (pcFrustum.isInFrustum(sphere) && pcFrustum.isInFrustum(box)) {
							renderer.render(instLvl, be, pos, pct, camera.getPosition());
						}
					}
				}
			}
		}
		manager.drawFrame(instLvl);
		
		InstancerHooks.postEndInstancing();
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		PaCoRenderState.resetInstancerState();
	}
}
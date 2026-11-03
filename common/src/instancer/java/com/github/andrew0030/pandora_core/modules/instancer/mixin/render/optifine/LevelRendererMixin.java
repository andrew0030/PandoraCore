package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.optifine;

import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import com.github.andrew0030.pandora_core.modules.instancer.compat.InstancerHooks;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.itf.OptifineInstanceListAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements OptifineInstanceListAccessor {
	@Shadow @Nullable private ClientLevel level;
	@Shadow @Final private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
	@Shadow public Frustum cullingFrustum;
	@Shadow @Nullable public Frustum capturedFrustum;

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderedEntities:I", ordinal = 0), method = "renderLevel")
	public void preRenderEnts(PoseStack stack, float partialTick, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
//		if (ShaderChecker.isShaderActive()) return;

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

		InstanceManager manager = ((PacoInstancingLevel) level).pandoraCore$getManager();
		manager.markFrame();
//		// TODO: optimize this loop
		for(Entity entity : this.level.entitiesForRendering()) {
			EntityTypeAttachments attachments = ((EntityTypeAttachments) entity.getType());
			InstancedEntityRenderer renderer = attachments.pandoraCore$getInstancedRenderer();
			if (renderer != null) {
				if (renderer.shouldRender(
						entity, camera.getPosition()
				)) {
					renderer.render((PacoInstancingLevel) level, entity, entity.getPosition(partialTick), partialTick, camera.getPosition());
				}
			}
		}
		manager.drawFrame((PacoInstancingLevel) level);

		InstancerHooks.postEndInstancing();

		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();

//		PaCoRenderState.resetInstancerState();

		PaCoRenderState.ACTIVE_ENVIRONMENT = (PacoInstancingLevel) level;
		manager.markFrame();
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V", ordinal = 3, shift = At.Shift.AFTER), method = "renderLevel")
	public void postRenderEnts(PoseStack stack, float partialTick, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
//		if (ShaderChecker.isShaderActive()) return;

		InstanceManager manager = ((PacoInstancingLevel) level).pandoraCore$getManager();
		manager.drawFrame((PacoInstancingLevel) level);
		PaCoRenderState.resetInstancerState();

		PaCoRenderState.ACTIVE_ENVIRONMENT = null;
	}

//	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderInfosTileEntities:Ljava/util/List;", ordinal = 0), method = "renderLevel")
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;updateTextRenderDistance()V", ordinal = 0, shift = At.Shift.AFTER, remap = false), method = "renderLevel")
	public void preRenderBlockEnts(PoseStack stack, float partialTick, long finishNano, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
//		if (ShaderChecker.isShaderActive()) return;

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

		InstanceManager manager = ((PacoInstancingLevel) level).pandoraCore$getManager();
		manager.markFrame();
		List<LevelRenderer.RenderChunkInfo> infs = this.pandoraCore$renderInfosInstancer;
		PacoInstancingLevel instLvl = (PacoInstancingLevel) level;
		Frustum frustum = capturedFrustum == null ? cullingFrustum : capturedFrustum;
		PaCoFrustum pcFrustum = (PaCoFrustum) frustum;
		
		CullBox box = new CullBox(0, 0, 0, 0, 0, 0);
		CullSphere sphere = new CullSphere(0, 0, 0, 0);
		
		for (LevelRenderer.RenderChunkInfo info : infs) {
			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
			List<BlockEntity> bes = ((InstancingResults) chnk).pandoraCore$getAll();
			if (bes.isEmpty()) continue; // no work to do
			
			AABB aabb = info.chunk.getBoundingBox();
			
			if (bes.size() < 4 || pcFrustum.pandoraCore$containsAllCorners(box.set(aabb))) {
				for (BlockEntity be : bes) {
					InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
					
					if (renderer.shouldRender(
							be, camera.getPosition()
					)) {
						BlockPos pos = be.getBlockPos();
						renderer.render(instLvl, be, pos, partialTick, camera.getPosition());
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
						
						if (pcFrustum.pandoraCore$isInFrustum(sphere) && pcFrustum.pandoraCore$isInFrustum(box)) {
							renderer.render(instLvl, be, pos, partialTick, camera.getPosition());
						}
					}
				}
			}
		}
		manager.drawFrame((PacoInstancingLevel) level);

		InstancerHooks.postEndInstancing();

		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();

		PaCoRenderState.resetInstancerState();
	}

	@Unique private ObjectArrayList<LevelRenderer.RenderChunkInfo> pandoraCore$renderInfosInstancer = new ObjectArrayList<>(1024);
	@Unique private final ObjectArrayList<LevelRenderer.RenderChunkInfo> pandoraCore$renderInfosInstancerNormal = new ObjectArrayList<>(1024);
	@Unique private final ObjectArrayList<LevelRenderer.RenderChunkInfo> pandoraCore$renderInfosInstancerShadow = new ObjectArrayList<>(1024);

	@WrapOperation(
			method = "applyFrustum",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer$RenderChunkInfo;chunk:Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;", remap = true),
			remap = false
	)
	public ChunkRenderDispatcher.RenderChunk wrapGetChunk(LevelRenderer.RenderChunkInfo inf, Operation<ChunkRenderDispatcher.RenderChunk> original) {
		ChunkRenderDispatcher.RenderChunk chnk = original.call(inf);
		ChunkRenderDispatcher.CompiledChunk instance = chnk.getCompiledChunk();
		
		if (!((InstancingResults) instance).pandoraCore$getAll().isEmpty()) {
			pandoraCore$renderInfosInstancer.add(inf);
		}

		return chnk;
	}

//	@Inject(
//			method = "applyFrustum",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;clearRenderInfosTerrain()V", remap = true),
//			remap = false
//	)
//	public void doClear(Frustum frustumIn, boolean updateRenderInfos, int maxChunkDistance, CallbackInfo ci) {
//		renderInfosInstancer.clear();
//	}
	
	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;isDirty()Z", remap = true),
			method = "updateRenderChunks",
			remap = false
	)
	public boolean instancerCondition(ChunkRenderDispatcher.RenderChunk inf, Operation<Boolean> original) {
		ChunkRenderDispatcher.CompiledChunk instance = inf.getCompiledChunk();
		
		if (!((InstancingResults) instance).pandoraCore$getAll().isEmpty()) {
			return true;
		}
		
		return original.call(inf);
	}

	@Inject(
			at = @At("RETURN"),
			method = "clearRenderInfosTerrain",
			remap = false
	)
	public void postClear(CallbackInfo ci) {
		pandoraCore$renderInfosInstancer.clear();
	}

	@Inject(
			at = @At("TAIL"),
			method = "setShadowRenderInfos",
			remap = false
	)
	public void postSetSI(boolean shadowInfos, CallbackInfo ci) {
		if (shadowInfos) {
			this.pandoraCore$renderInfosInstancer = this.pandoraCore$renderInfosInstancerShadow;
		} else {
			this.pandoraCore$renderInfosInstancer = this.pandoraCore$renderInfosInstancerNormal;
		}
	}
	
	@Override
	public ObjectArrayList<LevelRenderer.RenderChunkInfo> pandoraCore$getRenderInfosInstancer() {
		return pandoraCore$renderInfosInstancer;
	}
	
	@Override
	public ClientLevel pandoraCore$getLevel() {
		return level;
	}
}
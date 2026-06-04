package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.optifine;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.optifine.shaders.Shaders;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	@Final
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
	
	@Shadow(remap = false, aliases = "")
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderInfosTerrain;
	@Shadow(remap = false, aliases = "")
	private LongOpenHashSet renderInfosEntities;
	@Shadow(remap = false, aliases = "")
	private List<LevelRenderer.RenderChunkInfo> renderInfosTileEntities;
	@Shadow(remap = false, aliases = "")
	private ObjectArrayList renderInfosTerrainNormal;
	@Shadow(remap = false, aliases = "")
	private LongOpenHashSet renderInfosEntitiesNormal;
	@Shadow(remap = false, aliases = "")
	private List renderInfosTileEntitiesNormal;
	@Shadow(remap = false, aliases = "")
	private ObjectArrayList renderInfosTerrainShadow;
	@Shadow(remap = false, aliases = "")
	private LongOpenHashSet renderInfosEntitiesShadow;
	@Shadow(remap = false, aliases = "")
	private List renderInfosTileEntitiesShadow;
	
	@Shadow
	@Nullable
	private ClientLevel level;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderedEntities:I", ordinal = 0), method = "renderLevel")
	public void preRenderEnts(PoseStack stack, float $$1, long $$2, boolean $$3, Camera $$4, GameRenderer $$5, LightTexture $$6, Matrix4f $$7, CallbackInfo ci) {
		PaCoRenderState.setupWorld();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
		RenderSystem.getModelViewStack().translate(
				-$$4.getPosition().x,
				-$$4.getPosition().y,
				-$$4.getPosition().z
		);
		RenderSystem.applyModelViewMatrix();
		
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		List<LevelRenderer.RenderChunkInfo> infs = this.renderInfosInstancer;
//		System.out.println("aaa");
		for (
				LevelRenderer.RenderChunkInfo info : infs
		) {
			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
			for (BlockEntity be : ((InstancingResults) chnk).getAll()) {
				InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
				if (renderer.shouldRender(
						be, $$4.getPosition()
				)) {
					renderer.render(level, be, be.getBlockPos(), $$1);
				}
			}
		}
		manager.drawFrame(level);
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		PaCoRenderState.resetInstancerState();
	}
	
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderInfosInstancer = new ObjectArrayList<>(1024);
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderInfosInstancerNormal = new ObjectArrayList<>(1024);
	private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderInfosInstancerShadow = new ObjectArrayList<>(1024);
	
	@WrapOperation(
			method = "applyFrustum",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer$RenderChunkInfo;chunk:Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;", remap = true),
			remap = false
	)
	public ChunkRenderDispatcher.RenderChunk wrapGetChunk(
			LevelRenderer.RenderChunkInfo inf,
			Operation<ChunkRenderDispatcher.RenderChunk> original
	) {
		ChunkRenderDispatcher.RenderChunk chnk = original.call(inf);
		ChunkRenderDispatcher.CompiledChunk instance = chnk.getCompiledChunk();
		
		if (!((InstancingResults) instance).getAll().isEmpty()) {
			renderInfosInstancer.add(inf);
		}
		
		return chnk;
	}
	
	@Inject(
			at = @At("TAIL"),
			method = "clearRenderInfos",
			remap = false
	)
	public void postClear(CallbackInfo ci) {
		renderInfosInstancer.clear();
	}
	
	@Inject(
			at = @At("TAIL"),
			method = "setShadowRenderInfos",
			remap = false
	)
	public void postSetSI(boolean shadowInfos, CallbackInfo ci) {
		if (shadowInfos) {
			this.renderInfosInstancer = this.renderInfosInstancerShadow;
		} else {
			this.renderInfosInstancer = this.renderInfosInstancerNormal;
		}
	}
}

package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.iris;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoShadowRendererAccessor;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import com.github.andrew0030.pandora_core.modules.instancer.compat.InstancerHooks;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.SodiumRendererAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.frustum.FrustumHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowRendererMixin implements IPaCoShadowRendererAccessor {
	@Shadow
	private FrustumHolder entityFrustumHolder;
	@Unique LevelRendererAccessor pandoraCore$renderer;

    @Override
    public ClientLevel getActiveLevel() {
        return pandoraCore$renderer.getLevel();
    }

    @Override
    public LevelRendererAccessor getRenderer() {
        return pandoraCore$renderer;
    }

    @Inject(at = @At("HEAD"), method = "renderShadows")
    public void preRender(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        this.pandoraCore$renderer = levelRenderer;
    }
	
	@Inject(at = @At("HEAD"), method = "renderEntities")
	private void preRenderEntities(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
		ClientLevel level = getActiveLevel();
		
		PaCoRenderState.setupWorld();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(modelView.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(modelView.last().normal());
		RenderSystem.applyModelViewMatrix();
		
		InstancerHooks.preStartInstancing();
		
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		// TODO: optimize this loop
		Vec3 cameraPos = new Vec3(cameraX, cameraY, cameraZ);
		for(Entity entity : level.entitiesForRendering()) {
			EntityTypeAttachments attachments = ((EntityTypeAttachments) entity.getType());
			InstancedEntityRenderer renderer = attachments.pandoraCore$getInstancedRenderer();
			if (renderer != null) {
				if (renderer.shouldRender(
						entity, cameraPos
				)) {
					renderer.render((PacoInstancingLevel) level, entity, entity.getPosition(tickDelta), tickDelta, cameraPos);
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
	
	@Inject(at = @At("TAIL"), method = "renderEntities")
	private void postRenderEntities(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
		ClientLevel level = getActiveLevel();

		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.drawFrame((PacoInstancingLevel) level);
		InstancerHooks.postEndInstancing();
		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = null;
	}
	
	@Override
	public Frustum getEntityFrustum() {
		return entityFrustumHolder.getFrustum();
	}
}

package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.iris;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoShadowRendererAccessor;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullBox;
import com.github.andrew0030.pandora_core.modules.fastlib.render.CullSphere;
import com.github.andrew0030.pandora_core.modules.fastlib.render.PaCoFrustum;
import com.github.andrew0030.pandora_core.modules.instancer.compat.InstancerHooks;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.RenderListAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.sodium.SodiumRendererAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
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
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.shadows.frustum.advanced.AdvancedShadowCullingFrustum;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(value = ShadowRenderingState.class, remap = false)
public abstract class ShadowPassMixin {
	@Inject(at = @At("HEAD"), method = "renderBlockEntities")
	private static void preRenderBEs(ShadowRenderer shadowRenderer, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, Camera camera, double cameraX, double cameraY, double cameraZ, float tickDelta, boolean hasEntityFrustum, boolean lightsOnly, CallbackInfoReturnable<Integer> cir) {
	    PaCoRenderState.setupWorld();
		
        ClientLevel level = ((IPaCoShadowRendererAccessor) shadowRenderer).getActiveLevel();
        LevelRendererAccessor levelRenderer = ((IPaCoShadowRendererAccessor) shadowRenderer).getRenderer();
		
		SodiumWorldRenderer renderer = ((WorldRendererExtended) levelRenderer).sodium$getWorldRenderer();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		PacoInstancingLevel instancingLevel = (PacoInstancingLevel) level;
		InstanceManager manager = instancingLevel.getManager();
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(modelView.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(modelView.last().normal());
		RenderSystem.applyModelViewMatrix();
		
		InstancerHooks.preStartInstancing();
		
		manager.markFrame();
		SortedRenderLists renderLists = ((SodiumRendererAccessor) renderer).pandoraCore$sectionManager().getRenderLists();
		Iterator<ChunkRenderList> renderListIterator = renderLists.iterator();
		
		Frustum frustum = ((IPaCoShadowRendererAccessor) shadowRenderer).getEntityFrustum();
		PacoInstancingLevel instLvl = (PacoInstancingLevel) level;
		
		if (!hasEntityFrustum || frustum == null) {
			renderListIterator.forEachRemaining(renderList -> {
				ByteIterator iterator = ((RenderListAttachments) renderList).sectionsWithInstancableBEsIterator(false);
				if (iterator == null) return;
				
				while (iterator.hasNext()) {
					int element = iterator.nextByteAsInt();
					RenderSection section = renderList.getRegion().getSection(element);
					
					for (BlockEntity be : ((InstancingResults) section).getAll()) {
						InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
						if (beRenderer.shouldRender(
								be, camera.getPosition()
						)) {
							beRenderer.render(instLvl, be, be.getBlockPos(), tickDelta, camera.getPosition());
						}
					}
				}
			});
		} else {
			PaCoFrustum pcFrustum = (PaCoFrustum) frustum;
			
			CullBox box = new CullBox(0, 0, 0, 0, 0, 0);
			CullSphere sphere = new CullSphere(0, 0, 0, 0);
			
			// TODO: box culler mixin, no culler mixin
			// false inspection: mixin invalidates this
			//noinspection ConstantValue
			if (
					frustum.getClass().equals(Frustum.class) ||
							frustum.getClass().equals(AdvancedShadowCullingFrustum.class)
			) {
				renderListIterator.forEachRemaining(renderList -> {
					ByteIterator iterator = ((RenderListAttachments) renderList).sectionsWithInstancableBEsIterator(false);
					if (iterator == null) return;
					
					while (iterator.hasNext()) {
						int element = iterator.nextByteAsInt();
						RenderSection section = renderList.getRegion().getSection(element);
						
						List<BlockEntity> bes = ((InstancingResults) section).getAll();
						
						if (bes.size() < 4 || pcFrustum.containsAllCorners(box.set(
								section.getPosition().minBlockX() - camera.getPosition().x,
								section.getPosition().minBlockY() - camera.getPosition().y,
								section.getPosition().minBlockZ() - camera.getPosition().z,
								section.getPosition().maxBlockX() - camera.getPosition().x,
								section.getPosition().maxBlockY() - camera.getPosition().y,
								section.getPosition().maxBlockZ() - camera.getPosition().z
						))) {
							for (BlockEntity be : bes) {
								InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
								if (beRenderer.shouldRender(
										be, camera.getPosition()
								)) {
									beRenderer.render(instLvl, be, be.getBlockPos(), tickDelta, camera.getPosition());
								}
							}
						} else {
							for (BlockEntity be : bes) {
								InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
								
								if (beRenderer.shouldRender(
										be, camera.getPosition()
								)) {
									BlockPos pos = be.getBlockPos();
									beRenderer.getCullBox(box, instLvl, be, pos);
									sphere.contain(box);
									
									if (pcFrustum.isInFrustum(sphere) && pcFrustum.isInFrustum(box)) {
										beRenderer.render(instLvl, be, pos, tickDelta, camera.getPosition());
									}
								}
							}
						}
					}
				});
			} else {
				renderListIterator.forEachRemaining(renderList -> {
					ByteIterator iterator = ((RenderListAttachments) renderList).sectionsWithInstancableBEsIterator(false);
					if (iterator == null) return;
					
					while (iterator.hasNext()) {
						int element = iterator.nextByteAsInt();
						RenderSection section = renderList.getRegion().getSection(element);
						
						List<BlockEntity> bes = ((InstancingResults) section).getAll();
						
						if (bes.size() < 4) {
							for (BlockEntity be : bes) {
								InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
								if (beRenderer.shouldRender(
										be, camera.getPosition()
								)) {
									beRenderer.render(instLvl, be, be.getBlockPos(), tickDelta, camera.getPosition());
								}
							}
						} else {
							for (BlockEntity be : bes) {
								InstancedBlockEntityRenderer beRenderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
								
								if (beRenderer.shouldRender(
										be, camera.getPosition()
								)) {
									BlockPos pos = be.getBlockPos();
									beRenderer.getCullBox(box, instLvl, be, pos);
									sphere.contain(box);
									
									if (pcFrustum.isInFrustum(box)) {
										beRenderer.render(instLvl, be, pos, tickDelta, camera.getPosition());
									}
								}
							}
						}
					}
				});
			}
		}
		manager.drawFrame((PacoInstancingLevel) level);
		
		InstancerHooks.postEndInstancing();
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		PaCoRenderState.resetInstancerState();
    }
}

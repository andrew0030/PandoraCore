package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.sodium;

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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class SodiumRendererMixin implements SodiumRendererAccessor {
    @Shadow private ClientLevel world;
    @Shadow private RenderSectionManager renderSectionManager;

	@Inject(at = @At("HEAD"), method = "drawChunkLayer", require = 0)
	public void preDrawLayer(RenderType renderLayer, PoseStack matrixStack, double x, double y, double z, CallbackInfo ci) {
	
	}
	
	@Inject(at = @At("HEAD"), method = "renderBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;Lnet/minecraft/client/Camera;F)V")
    public void preRenderBEs(PoseStack matrices, RenderBuffers bufferBuilders, Long2ObjectMap<SortedSet<BlockDestructionProgress>> blockBreakingProgressions, Camera camera, float tickDelta, CallbackInfo ci) {
	    PaCoRenderState.setupWorld();
		
        PacoInstancingLevel instancingLevel = (PacoInstancingLevel) world;
        InstanceManager manager = instancingLevel.getManager();

        Lighting.setupLevel(RenderSystem.getModelViewMatrix());
        RenderSystem.setupShaderLights(
                GameRenderer.getRendertypeEntitySolidShader()
        );

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().last().pose().mul(matrices.last().pose());
        RenderSystem.getModelViewStack().last().normal().mul(matrices.last().normal());
        RenderSystem.applyModelViewMatrix();
	    
	    InstancerHooks.preStartInstancing();
		
        manager.markFrame();
        SortedRenderLists renderLists = this.renderSectionManager.getRenderLists();
        Iterator<ChunkRenderList> renderListIterator = renderLists.iterator();
		
		PacoInstancingLevel instLvl = (PacoInstancingLevel) world;
		PaCoFrustum pcFrustum = (PaCoFrustum) (Minecraft.getInstance().levelRenderer.capturedFrustum == null ? Minecraft.getInstance().levelRenderer.cullingFrustum : Minecraft.getInstance().levelRenderer.capturedFrustum);
		
		CullBox box = new CullBox(0, 0, 0, 0, 0, 0);
		CullSphere sphere = new CullSphere(0, 0, 0, 0);
		
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
						InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
						if (renderer.shouldRender(
								be, camera.getPosition()
						)) {
							renderer.render(instLvl, be, be.getBlockPos(), tickDelta, camera.getPosition());
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
								renderer.render(instLvl, be, pos, tickDelta, camera.getPosition());
							}
						}
					}
				}
			}
		});
        manager.drawFrame((PacoInstancingLevel) world);
		
	    InstancerHooks.postEndInstancing();
		
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
		
	    PaCoRenderState.resetInstancerState();
    }

    @Override
    public RenderSectionManager pandoraCore$sectionManager() {
        return renderSectionManager;
    }
}
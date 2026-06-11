package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.iris;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoShadowRendererAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.EntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedEntityRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowRendererMixin implements IPaCoShadowRendererAccessor {
	LevelRendererAccessor renderer;

    @Override
    public ClientLevel getActiveLevel() {
        return renderer.getLevel();
    }

    @Override
    public LevelRendererAccessor getRenderer() {
        return renderer;
    }

    @Inject(at = @At("HEAD"), method = "renderShadows")
    public void preRender(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        this.renderer = levelRenderer;
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
//		RenderSystem.getModelViewStack().translate(
//				-$$4.getPosition().x,
//				-$$4.getPosition().y,
//				-$$4.getPosition().z
//		);
		RenderSystem.applyModelViewMatrix();
		
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
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();

//		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = (PacoInstancingLevel) level;
		manager.markFrame();
	}
	
	@Inject(at = @At("TAIL"), method = "renderEntities")
	private void postRenderEntities(LevelRendererAccessor levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource bufferSource, PoseStack modelView, float tickDelta, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
		ClientLevel level = getActiveLevel();

		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.drawFrame((PacoInstancingLevel) level);
		PaCoRenderState.resetInstancerState();
		
		PaCoRenderState.ACTIVE_ENVIRONMENT = null;
	}
}

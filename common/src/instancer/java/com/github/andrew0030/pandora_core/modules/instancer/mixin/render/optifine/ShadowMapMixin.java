package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.optifine;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.optifine.shaders.ShadersRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ShadersRender.class)
public class ShadowMapMixin {
	@Unique private static float pandoraCore$spct;
	@Unique private static Camera pandoraCore$camera;
	@Unique private static PoseStack pandoraCore$stack;
	
	@WrapOperation(
			method = "renderShadowMap",
			at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;setCameraShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V"),
			remap = false
	)
	private static void matrix(
			PoseStack matrixStack, Camera activeRenderInfo, float partialTicks, Operation<Void> original
	) {
		pandoraCore$stack = matrixStack;
		pandoraCore$spct = partialTicks;
		pandoraCore$camera = activeRenderInfo;
		
		original.call(matrixStack, activeRenderInfo, partialTicks);
	}
	
	@WrapOperation(
			method = "renderShadowMap",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;getRenderInfosTileEntities()Ljava/util/List;"),
			remap = false
	)
	private static List preRenderEnts(
			LevelRenderer instance,
			Operation<List> original
	) {
//		PaCoRenderState.setupWorld();
//
//		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
//		RenderSystem.setupShaderLights(
//				GameRenderer.getRendertypeEntitySolidShader()
//		);
//
//		RenderSystem.getModelViewStack().pushPose();
//		RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
//		RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
//		RenderSystem.applyModelViewMatrix();
//
//		InstancerHooks.preStartInstancing();
//
//		ClientLevel level = ((OptifineInstanceListAccessor)instance).getLevel();
//		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
//		manager.markFrame();
//		List<LevelRenderer.RenderChunkInfo> infs = ((OptifineInstanceListAccessor)instance).getRenderInfosInstancer();
//		for (
//				LevelRenderer.RenderChunkInfo info : infs
//		) {
//			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
//			for (BlockEntity be : ((InstancingResults) chnk).getAll()) {
//				InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
//				if (renderer.shouldRender(
//						be, camera.getPosition()
//				)) {
//					renderer.render((PacoInstancingLevel) level, be, be.getBlockPos(), spct, camera.getPosition());
//				}
//			}
//		}
//		manager.drawFrame((PacoInstancingLevel) level);
//
//		InstancerHooks.postEndInstancing();
//
//		RenderSystem.getModelViewStack().popPose();
//		RenderSystem.applyModelViewMatrix();
//
//		PaCoRenderState.resetInstancerState();
		
		return original.call(instance);
	}
}
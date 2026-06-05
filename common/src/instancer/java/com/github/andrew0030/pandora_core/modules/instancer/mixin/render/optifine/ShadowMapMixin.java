package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.optifine;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import com.github.andrew0030.pandora_core.modules.instancer.itf.OptifineInstanceListAccessor;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
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
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.optifine.shaders.ShadersRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ShadersRender.class)
public class ShadowMapMixin {
	private static float spct;
	private static Camera camera;
	private static PoseStack stack;
	
	@WrapOperation(
			method = "renderShadowMap",
			at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;setCameraShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V"),
			remap = false
	)
	private static void matrix(
			PoseStack matrixStack, Camera activeRenderInfo, float partialTicks, Operation<Void> original
	) {
		stack = matrixStack;
		spct = partialTicks;
		camera = activeRenderInfo;
		
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
		PaCoRenderState.setupWorld();
		
		Lighting.setupLevel(RenderSystem.getModelViewMatrix());
		RenderSystem.setupShaderLights(
				GameRenderer.getRendertypeEntitySolidShader()
		);
		
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().last().pose().mul(stack.last().pose());
		RenderSystem.getModelViewStack().last().normal().mul(stack.last().normal());
		RenderSystem.getModelViewStack().translate(
				-camera.getPosition().x,
				-camera.getPosition().y,
				-camera.getPosition().z
		);
		RenderSystem.applyModelViewMatrix();
		
		ClientLevel level = ((OptifineInstanceListAccessor)instance).getLevel();
		InstanceManager manager = ((PacoInstancingLevel) level).getManager();
		manager.markFrame();
		List<LevelRenderer.RenderChunkInfo> infs = ((OptifineInstanceListAccessor)instance).getRenderInfosInstancer();
//		System.out.println("aaa");
		for (
				LevelRenderer.RenderChunkInfo info : infs
		) {
			ChunkRenderDispatcher.CompiledChunk chnk = info.chunk.getCompiledChunk();
			for (BlockEntity be : ((InstancingResults) chnk).getAll()) {
				InstancedBlockEntityRenderer renderer = ((BlockEntityTypeAttachments) be.getType()).pandoraCore$getInstancedRenderer();
				if (renderer.shouldRender(
						be, camera.getPosition()
				)) {
					renderer.render(level, be, be.getBlockPos(), spct);
				}
			}
		}
		manager.drawFrame(level);
		
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		PaCoRenderState.resetInstancerState();
		
		return original.call(instance);
	}
}

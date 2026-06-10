package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.item;

import com.github.andrew0030.pandora_core.client.PaCoClientTicker;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin.ItemDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin.ItemInstancingEnv;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.ItemAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedItemRenderer;
import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@Unique
	private final ItemDrawData pandoraCore$data = new ItemDrawData();
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;isCustomRenderer()Z"), method = "render")
	public void preCheckCustomRenderer(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
		Item item = itemStack.getItem();
		ItemAttachments attch = (ItemAttachments) item;
		
		InstancedItemRenderer renderer = attch.pandoraCore$getInstancedRenderer();
		if (renderer == null) return; // not instancing this!
		
		InstancingEnvironment env = PaCoRenderState.ACTIVE_ENVIRONMENT;
		
		InstanceManager IMMEDIATE = null;
		
		if (env == null) {
			IMMEDIATE = new InstanceManager();
			env = new ItemInstancingEnv(IMMEDIATE);
		}
		
		poseStack.pushPose();
		pandoraCore$data.setMatr(poseStack);
		pandoraCore$data.setLightMap(combinedLight);
		pandoraCore$data.setDisplayContext(displayContext);
		
		renderer.render(
				env, itemStack,
				pandoraCore$data, PaCoClientTicker.getPartialTick(),
				null
		);
		poseStack.popPose();
		
		if (IMMEDIATE != null) {
			IMMEDIATE.drawFrame(env);
			IMMEDIATE.close();
		}
	}
}

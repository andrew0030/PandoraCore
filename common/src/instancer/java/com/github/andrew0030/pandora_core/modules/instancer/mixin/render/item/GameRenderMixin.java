package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.item;

import com.github.andrew0030.pandora_core.modules.instancer.state.PaCoRenderState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", shift = At.Shift.BEFORE), method = "render")
	public void preStartGUIRender(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
		PaCoRenderState.setupUI();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V", shift = At.Shift.AFTER), method = "render")
	public void postClearGUIRender(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
		PaCoRenderState.resetInstancerState();
	}
}

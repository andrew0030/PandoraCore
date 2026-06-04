package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFTemplatedProgram;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {
	@Inject(at = @At("HEAD"), method = "apply", cancellable = true)
	public void preApply(CallbackInfo ci) {
		if (OFTemplatedProgram.useProgram != null) {
			for (int i = 0; i < 1;i++) {
				GlStateManager._activeTexture(GL20.GL_TEXTURE0);
				GL20.glBindTexture(GL20.GL_TEXTURE_2D, RenderSystem.getShaderTexture(0));
			}
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "clear", cancellable = true)
	public void preClear(CallbackInfo ci) {
		if (OFTemplatedProgram.useProgram != null)
			ci.cancel();
	}
}

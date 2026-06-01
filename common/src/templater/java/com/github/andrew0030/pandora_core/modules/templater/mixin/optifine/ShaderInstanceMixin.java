package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFTemplatedProgram;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {
//	@Inject(at = @At("HEAD"), method = "apply", cancellable = true)
//	public void preApply(CallbackInfo ci) {
//		if (OFTemplatedProgram.useProgram != null)
//			ci.cancel();
//	}
//
//	@Inject(at = @At("HEAD"), method = "clear", cancellable = true)
//	public void preClear(CallbackInfo ci) {
//		if (OFTemplatedProgram.useProgram != null)
//			ci.cancel();
//	}
}

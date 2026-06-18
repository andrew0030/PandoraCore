package com.github.andrew0030.pandora_core.modules.templater.mixin.compat;

import com.github.andrew0030.pandora_core.modules.templater.compat.PatcherHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PatcherHooks.class, remap = false)
public class IPCompat {
	@Inject(at = @At("RETURN"), method = "disableCustomCore", cancellable = true)
	private static void onCheckCoreSupport(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}

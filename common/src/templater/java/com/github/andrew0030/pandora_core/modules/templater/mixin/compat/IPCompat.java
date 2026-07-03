package com.github.andrew0030.pandora_core.modules.templater.mixin.compat;

import com.github.andrew0030.pandora_core.modules.templater.compat.PatcherHooks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PatcherHooks.class, remap = false)
public class IPCompat {
	@ModifyReturnValue(method = "disableCustomCore", at = @At("RETURN"))
	private static boolean onCheckCoreSupport(boolean original) {
		return true;
	}
}
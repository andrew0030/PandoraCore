package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.dbg;

import net.optifine.shaders.DefaultShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(value = DefaultShaders.class, remap = false)
public class DefaultShadersMixin {
//	@Inject(at = @At("HEAD"), method = "getFinalVsh")
//	private static void preGetVSH(CallbackInfoReturnable<InputStream> cir) {
//		throw new RuntimeException("Time to self destruct!");
//	}
}

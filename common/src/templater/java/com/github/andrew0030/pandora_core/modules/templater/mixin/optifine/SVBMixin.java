package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFTemplatedProgram;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.optifine.shaders.SVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SVertexBuilder.class, remap = false)
public class SVBMixin {
	@Inject(at = @At("HEAD"), method = "preDrawArrays", cancellable = true)
	private static void preDA(VertexFormat vf, CallbackInfoReturnable<Boolean> cir) {
		if (OFTemplatedProgram.useProgram != null) {
			cir.setReturnValue(false);
		}
	}
}

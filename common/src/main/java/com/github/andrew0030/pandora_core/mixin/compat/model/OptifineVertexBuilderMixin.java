package com.github.andrew0030.pandora_core.mixin.compat.model;

import com.github.andrew0030.pandora_core.client.render.optifine.PaCoSVB;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.optifine.shaders.SVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SVertexBuilder.class, remap = false)
public class OptifineVertexBuilderMixin {
	@Inject(at = @At("HEAD"), method = "endAddVertex", cancellable = true)
	private static void preEnd(BufferBuilder builder, CallbackInfo ci) {
		SVertexBuilder builder1 = (SVertexBuilder) OptifineAccessor.getSVB(builder);
		if (builder1 instanceof PaCoSVB pSVB) {
			pSVB.pandoraCore$endAddVertex(builder);
			ci.cancel();
		}
	}
	
	@Inject(at = @At("HEAD"), method = "endAddVertexData", cancellable = true)
	private static void preEnd1(BufferBuilder builder, CallbackInfo ci) {
		SVertexBuilder builder1 = (SVertexBuilder) OptifineAccessor.getSVB(builder);
		if (builder1 instanceof PaCoSVB pSVB) {
			pSVB.pandoraCore$endAddVertexData(builder);
			ci.cancel();
		}
	}
}

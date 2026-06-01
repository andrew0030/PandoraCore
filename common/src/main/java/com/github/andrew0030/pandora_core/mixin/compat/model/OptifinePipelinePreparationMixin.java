package com.github.andrew0030.pandora_core.mixin.compat.model;

import com.github.andrew0030.pandora_core.client.render.obj.ObjLoader;
import net.optifine.shaders.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Shaders.class, remap = false)
public class OptifinePipelinePreparationMixin {
	@Inject(at = @At("TAIL"), method = "resetDisplayLists")
	private static void postInit(CallbackInfo ci) {
		ObjLoader.forceReload();
	}
}

package com.github.andrew0030.pandora_core.mixin.compat.model;

import com.github.andrew0030.pandora_core.client.render.obj.ObjLoader;
import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PipelineManager.class)
public class IrisPipelinePreparationMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;allChanged()V", shift = At.Shift.AFTER), method = "preparePipeline")
    public void preChangeAll(NamespacedId currentDimension, CallbackInfoReturnable<WorldRenderingPipeline> cir) {
        ObjLoader.forceReload();
    }
}

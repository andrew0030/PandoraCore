package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IrisRenderingPipeline.class)
public class IrisPipelineMixin {
    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(ProgramSet programSet, CallbackInfo ci) {
//        TemplateManager.reloadAll();
        IrisTemplateLoader.doLoad();
    }

//    @Inject(at = @At("HEAD"), method = "destroyShaders")
//    public void preDestroyShaders(CallbackInfo ci) {
//
//    }
}

package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public class ShaderInstance_CacheShadersMixin {
    // while this is tracked by vanilla
    // I'm making a copy of it as a safety incase another mod messes with it
    @Unique
    String pandoraCore$cacheName;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(ResourceProvider $$0, String $$1, VertexFormat $$2, CallbackInfo ci) {
        VanillaTemplateLoader.bindShader($$1, (ShaderInstance) (Object) this);
        pandoraCore$cacheName = $$1;
    }

    @Inject(at = @At("TAIL"), method = "close")
    public void preClose(CallbackInfo ci) {
        VanillaTemplateLoader.unbindShader(pandoraCore$cacheName, (ShaderInstance) (Object) this);
    }
}

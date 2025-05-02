package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class ShaderLoaderDeferenceMixin {
    @Inject(at = @At("TAIL"), method = "reloadShaders")
    public void postReload(ResourceProvider $$0, CallbackInfo ci) {
        VanillaTemplateLoader.getInstance().performReload();
    }
}

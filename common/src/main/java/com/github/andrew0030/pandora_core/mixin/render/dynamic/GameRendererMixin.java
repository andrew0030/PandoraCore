package com.github.andrew0030.pandora_core.mixin.render.dynamic;

import com.github.andrew0030.pandora_core.client.registry.PaCoRendererRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique private static boolean pandoraCore$dynamicRenderersAttached;

    // The end of reloadShaders should be a good spot to loop over all the dynamic renderers
    // as it happens after forge's client setup enqueue work, and before the main menu renders
    @Inject(method = "reloadShaders", at = @At("TAIL"))
    public void onShaderReload(ResourceProvider resourceProvider, CallbackInfo ci) {
        // Got to make sure the renderers only get attached once!
        if (!pandoraCore$dynamicRenderersAttached) {
            PaCoRendererRegistry.registerInternal();
            pandoraCore$dynamicRenderersAttached = true;
        }
    }
}
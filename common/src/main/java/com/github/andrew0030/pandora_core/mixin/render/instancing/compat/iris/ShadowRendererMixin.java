package com.github.andrew0030.pandora_core.mixin.render.instancing.compat.iris;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoShadowRendererAccessor;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShadowRenderer.class, remap = false)
public abstract class ShadowRendererMixin implements IPaCoShadowRendererAccessor {
    LevelRendererAccessor renderer;

    @Override
    public ClientLevel getActiveLevel() {
        return renderer.getLevel();
    }

    @Override
    public LevelRendererAccessor getRenderer() {
        return renderer;
    }

    @Inject(at = @At("HEAD"), method = "renderShadows")
    public void preRender(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        this.renderer = levelRenderer;
    }
}

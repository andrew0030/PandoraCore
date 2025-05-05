package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ShaderLoaderDeferenceMixin {
    @Inject(at = @At("HEAD"), method = "onGameLoadFinished")
    public void postReload(CallbackInfo ci) {
        VanillaTemplateLoader.getInstance().performReload();
    }
}

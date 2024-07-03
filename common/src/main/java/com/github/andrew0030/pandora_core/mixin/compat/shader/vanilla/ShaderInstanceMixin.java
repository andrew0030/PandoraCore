package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {
    @Inject(at = @At("HEAD"), method = "getOrCreate")
    private static void preGetOrCreate(ResourceProvider pResourceProvider, Program.Type pProgramType, String pName, CallbackInfoReturnable<Program> cir) {
//        String file = name + pProgramType.getExtension();
        try {
            ResourceLocation loc = new ResourceLocation(pName);
            VanillaTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + pProgramType.getExtension());
        } catch (Throwable err) {
            VanillaTemplateLoader.activeFile("unknown", pName);
        }
    }
}

package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderInstance.class)
public class ShaderInstance_CacheSrcMixin {
    @Shadow
    private static int lastProgramId;
    @Shadow private static ShaderInstance lastAppliedShader;

    // while this is tracked by vanilla
    // I'm making a copy of it as a safety incase another mod messes with it
    @Unique
    String pandoraCore$cacheName;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(ResourceProvider resourceProvider, String name, VertexFormat vertexFormat, CallbackInfo ci) {
        pandoraCore$cacheName = name;
    }

    @Inject(at = @At("HEAD"), method = "getOrCreate")
    private static void preGetOrCreate(ResourceProvider pResourceProvider, Program.Type pProgramType, String pName, CallbackInfoReturnable<Program> cir) {
        try {
            ResourceLocation loc = new ResourceLocation(pName);
            IrisTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + pProgramType.getExtension());
        } catch (Throwable err) {
            IrisTemplateLoader.activeFile("unknown", pName);
        }
    }

    @Inject(at = @At("TAIL"), method = "close")
    public void preClose(CallbackInfo ci) {
        IrisTemplateLoader.unbindShader(pandoraCore$cacheName, (ShaderInstance) (Object) this);
    }
}
package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.PandoraCoreClient;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderInstance.class)
public class ShaderInstance_CacheSrc_CondBindableMixin implements IPaCoConditionallyBindable {
    @Shadow
    private static int lastProgramId;

    @Shadow
    @Final
    private int programId;

    @Shadow private static ShaderInstance lastAppliedShader;

    @Inject(at = @At("HEAD"), method = "getOrCreate")
    private static void preGetOrCreate(ResourceProvider pResourceProvider, Program.Type pProgramType, String pName, CallbackInfoReturnable<Program> cir) {
        try {
            ResourceLocation loc = new ResourceLocation(pName);
            // TODO: this is ugly and dumb
            //       redo!
            if (loc.getPath().startsWith(PandoraCoreClient.SHADER_PATH_PREFIX)) {
                VanillaTemplateLoader.activeFile(
                        loc.getNamespace(),
                        loc.getPath() + pProgramType.getExtension()
                );
            } else {
                VanillaTemplateLoader.activeFile(
                        loc.getNamespace(),
                        PandoraCoreClient.SHADER_PATH_PREFIX + loc.getPath() + pProgramType.getExtension()
                );
            }
        } catch (Throwable err) {
            VanillaTemplateLoader.activeFile("unknown", pName);
        }
    }

    @Unique
    boolean pandoraCore$disableBind = false;

    @Inject(at = @At("HEAD"), method = "apply")
    public void preApply(CallbackInfo ci) {
        if (pandoraCore$disableBind)
            lastProgramId = programId;
    }

    @Override
    public void pandoraCore$disableBind() {
        pandoraCore$disableBind = true;
    }

    @Override
    public void pandoraCore$enableBind() {
        pandoraCore$disableBind = false;
        lastProgramId = 0;
        lastAppliedShader = null;
    }

    @Override
    public boolean isDisableBind() {
        return pandoraCore$disableBind;
    }
}

package com.github.andrew0030.pandora_core.mixin.fixes;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* Author: GiantLuigi4 */
/* this should be a part of fapi */
@Mixin(EffectInstance.class)
public class EffectInstanceMixin {
    @Unique
    protected ResourceLocation trueId;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"))
    public void preMakeResourceName(ResourceManager resourceManager, String string, CallbackInfo ci) {
        ResourceLocation nameId = new ResourceLocation(string);
        trueId = new ResourceLocation(
                nameId.getNamespace(),
                "shaders/program/" + nameId.getPath() + ".json"
        );
    }

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"))
    public void modifyResourceName(Args args) {
        args.set(0, trueId.toString());
    }

    @Unique
    private static ResourceLocation idLoad = null;

    @ModifyArgs(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"))
    private static void modifyFileId(Args args) {
        if (!args.get(0).toString().contains(":")) idLoad = null;
        else {
            String name = args.get(0).toString().substring("shaders/program/".length());
            String nameWithType = name;
            name = name.substring(0, name.length() - (".vsh".length()));
            ResourceLocation nameId = new ResourceLocation(name);
            idLoad = new ResourceLocation(
                    nameId.getNamespace(),
                    "shaders/program/" + nameId.getPath() + nameWithType.substring(name.length())
            );
            args.set(0, idLoad.toString());
        }
    }

    @ModifyArgs(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/shaders/EffectProgram;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/EffectProgram;"))
    private static void modifyName(Args args) {
        if (idLoad == null) return;
        args.set(1, idLoad.toString());
    }
}
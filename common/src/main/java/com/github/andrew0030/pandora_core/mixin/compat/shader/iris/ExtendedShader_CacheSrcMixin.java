package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.IrisTemplateLoader;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.sodium.impl.shader_overrides.IrisShaderTypes;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

@Mixin(value = ExtendedShader.class, remap = false)
public class ExtendedShader_CacheSrcMixin {
    // TODO: this does not need to be a redirect
    @SuppressWarnings({"InvalidInjectorMethodSignature", "UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/shaders/Program;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)Lcom/mojang/blaze3d/shaders/Program;"
            ),
            method = "lambda$iris$createExtraShaders$0"
    )
    protected Program wrapCreateShader(Program.Type pType, String pName, InputStream pShaderData, String pSourceName, GlslPreprocessor pPreprocessor) {
        try {
            ResourceLocation loc = new ResourceLocation(pName);
            IrisTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + ".gsh");

            return Program.compileShader(
                    pType, pName,
                    pShaderData, pSourceName,
                    pPreprocessor
            );
        } catch (Throwable err) {
            Iris.logger.error("Failed to create shader program", err);
            throw new RuntimeException("failed to load program");
        }
    }
}

package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ExtendedShader.class)
public class ShaderInstance_CacheShadersMixin {
    // while this is tracked by vanilla
    // I'm making a copy of it as a safety incase another mod messes with it
    @Unique
    String pandoraCore$cacheName;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(ResourceProvider resourceFactory, String string, VertexFormat vertexFormat, boolean usesTessellation, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, BlendModeOverride blendModeOverride, AlphaTest alphaTest, Consumer uniformCreator, BiConsumer samplerCreator, boolean isIntensity, IrisRenderingPipeline parent, List bufferBlendOverrides, CustomUniforms customUniforms, CallbackInfo ci) {
        IrisTemplateLoader.bindShader(string, (ShaderInstance) (Object) this);
    }
}

package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.IrisTemplatedShader;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoConditionallyBindable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoAccessInitializer;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoUniformIniitalizerAccessor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ExtendedShader.class)
public class ExtendedShader_CacheShaders_CondBindMixin {
    @Shadow
    private static ExtendedShader lastApplied;

    @Shadow
    @Final
    private ProgramSamplers samplers;

    @Shadow
    @Final
    private ProgramImages images;

    @Shadow
    @Final
    private ProgramUniforms uniforms;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(ResourceProvider resourceFactory, String string, VertexFormat vertexFormat, boolean usesTessellation, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, BlendModeOverride blendModeOverride, AlphaTest alphaTest, Consumer uniformCreator, BiConsumer samplerCreator, boolean isIntensity, IrisRenderingPipeline parent, List bufferBlendOverrides, CustomUniforms customUniforms, CallbackInfo ci) {
        IrisTemplateLoader.bindShader(string, (ShaderInstance) (Object) this);


        bakSamplers = ((IPacoAccessInitializer) samplers).pandoraCore$getInitializer();
        bakImages = ((IPacoAccessInitializer) images).pandoraCore$getInitializer();
        bakUniforms = ((IPacoUniformIniitalizerAccessor) uniforms).pandoraCore$getInitializer();
    }

    @Unique
    List<GlUniform1iCall> bakSamplers = new ArrayList<>();
    @Unique
    List<GlUniform1iCall> bakImages = new ArrayList<>();
    @Unique
    ImmutableList<Uniform> bakUniforms;
    @Unique
    boolean firstApply = false;

    @Inject(at = @At("HEAD"), method = "apply")
    public void preApply(CallbackInfo ci) {
        if (((IPaCoConditionallyBindable) this).isDisableBind()) {
            lastApplied = (ExtendedShader) (Object) this;

            firstApply = ((IPacoAccessInitializer) samplers).pandoraCore$getInitializer() != null;

            if (!IrisTemplatedShader.isFirstBind()) {
                ((IPacoAccessInitializer) samplers).pandoraCore$setInitializer(null);
                ((IPacoAccessInitializer) images).pandoraCore$setInitializer(null);
                ((IPacoUniformIniitalizerAccessor) uniforms).pandoraCore$setInitializer(null);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "apply")
    public void postApply(CallbackInfo ci) {
        if (((IPaCoConditionallyBindable) this).isDisableBind()) {
            if (IrisTemplatedShader.isFirstBind()) {
                IrisTemplatedShader.setBound();
            }

            if (firstApply) {
                ((IPacoAccessInitializer) samplers).pandoraCore$setInitializer(bakSamplers);
                ((IPacoAccessInitializer) images).pandoraCore$setInitializer(bakImages);
                ((IPacoUniformIniitalizerAccessor) uniforms).pandoraCore$setInitializer(bakUniforms);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "clear", cancellable = true)
    public void preClear(CallbackInfo ci) {
        if (((IPaCoConditionallyBindable) this).isDisableBind()) {
            lastApplied = null;
            ci.cancel();
        }
    }
}

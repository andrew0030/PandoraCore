package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoAccessInitializables;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExtendedShader.class)
public class ExtendedProgramMixin implements IPacoAccessInitializables {
    @Shadow
    @Final
    private ProgramSamplers samplers;

    @Shadow
    @Final
    private ProgramImages images;


    @Shadow
    @Final
    private ProgramUniforms uniforms;

    @Override
    public ProgramSamplers pandoraCore$getSamplers() {
        return samplers;
    }

    @Override
    public ProgramImages pandoraCore$getImages() {
        return images;
    }

    @Override
    public ProgramUniforms pandoraCore$getUniforms() {
        return uniforms;
    }
}

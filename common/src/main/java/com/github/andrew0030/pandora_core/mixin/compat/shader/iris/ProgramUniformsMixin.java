package com.github.andrew0030.pandora_core.mixin.compat.shader.iris;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.iris.IPaCoForceUploadable;
import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProgramUniforms.class)
public class ProgramUniformsMixin implements IPaCoForceUploadable {
    @Shadow @Final private ImmutableList<Uniform> perTick;

    @Shadow @Final private ImmutableList<Uniform> perFrame;

    @Shadow @Final private ImmutableList<Uniform> dynamic;

    public void pandoraCore$upload() {
        for (Uniform uniform : perTick) {
            uniform.update();
        }
        for (Uniform uniform : perFrame) {
            uniform.update();
        }
        for (Uniform uniform : dynamic) {
            uniform.update();
        }
    }
}

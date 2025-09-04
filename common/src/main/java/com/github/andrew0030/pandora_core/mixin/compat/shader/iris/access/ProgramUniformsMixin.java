package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoUniformIniitalizerAccessor;
import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ProgramUniforms.class)
public class ProgramUniformsMixin implements IPacoUniformIniitalizerAccessor {
    @Shadow private ImmutableList<Uniform> once;

    @Override
    public ImmutableList<Uniform> pandoraCore$getInitializer() {
        return once;
    }

    @Override
    public void pandoraCore$setInitializer(ImmutableList<Uniform> initializer) {
        once = initializer;
    }
}

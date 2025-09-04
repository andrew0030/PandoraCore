package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoAccessInitializer;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.program.ProgramImages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ProgramImages.class)
public class ProgramImagesMixin implements IPacoAccessInitializer {
    @Shadow private List<GlUniform1iCall> initializer;

    @Override
    public List<GlUniform1iCall> pandoraCore$getInitializer() {
        return initializer;
    }

    @Override
    public void pandoraCore$setInitializer(List<GlUniform1iCall> initializer) {
        this.initializer = initializer;
    }
}

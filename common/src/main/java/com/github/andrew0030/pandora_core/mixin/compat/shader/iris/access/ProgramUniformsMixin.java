package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoUniformInitalizerAccessor;
import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgramUniforms.class)
public class ProgramUniformsMixin implements IPacoUniformInitalizerAccessor {
    @Shadow private ImmutableList<Uniform> once;
    private ImmutableList<Uniform> cacheOnce;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(ImmutableList once, ImmutableList perTick, ImmutableList perFrame, ImmutableList dynamic, ImmutableList notifiersToReset, CallbackInfo ci) {
		cacheOnce = once;
	}
	
    @Override
    public ImmutableList<Uniform> pandoraCore$getInitializer() {
        return once;
    }
	
	@Override
	public ImmutableList<Uniform> pandoraCore$getCachedInitializer() {
		return cacheOnce;
	}
	
	@Override
    public void pandoraCore$setInitializer(ImmutableList<Uniform> initializer) {
        once = initializer;
    }
}

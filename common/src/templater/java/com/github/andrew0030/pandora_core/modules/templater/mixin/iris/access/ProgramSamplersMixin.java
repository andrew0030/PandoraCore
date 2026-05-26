package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoInitCachable;
import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.program.GlUniform1iCall;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.sampler.SamplerBinding;
import net.irisshaders.iris.gl.state.ValueUpdateNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProgramSamplers.class)
public class ProgramSamplersMixin implements IPacoInitCachable<List<GlUniform1iCall>> {
	@Shadow
	private List<GlUniform1iCall> initializer;
	@Unique
	private List<GlUniform1iCall> pandoraCore$trueInitializer;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(ImmutableList<SamplerBinding> samplerBindings, ImmutableList<ValueUpdateNotifier> notifiersToReset, List<GlUniform1iCall> initializer, CallbackInfo ci) {
		this.pandoraCore$trueInitializer = initializer;
	}
	
	@Override
	public List<GlUniform1iCall> pandoraCore$getInitializer() {
		return pandoraCore$trueInitializer;
	}
	
	@Override
	public List<GlUniform1iCall> pandoraCore$getCurrentInitializer() {
		return initializer;
	}
	
	@Override
	public void pandoraCore$setInitializer(List<GlUniform1iCall> initializer) {
		this.initializer = initializer;
	}
}

package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTrackerListable;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CustomUniforms.class)
public class CustomUniformsMixin implements IPaCoModTrackerListable {
	@Shadow
	@Final
	private List<CachedUniform> uniforms;
	
	@Override
	public Iterable<IPaCoModTracker> pandoraCore$listTrackers() {
		return (Iterable<IPaCoModTracker>) (Object) uniforms;
	}
}

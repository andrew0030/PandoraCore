package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.uniform.Uniform;

public interface IPacoUniformInitalizerAccessor {
	ImmutableList<Uniform> pandoraCore$getInitializer();
	
	ImmutableList<Uniform> pandoraCore$getCachedInitializer();
	
	void pandoraCore$setInitializer(ImmutableList<Uniform> initializer);
}

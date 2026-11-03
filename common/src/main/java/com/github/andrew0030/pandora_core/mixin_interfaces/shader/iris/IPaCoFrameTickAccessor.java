package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.uniform.Uniform;

public interface IPaCoFrameTickAccessor {
	long pandoraCore$getLastTick();
	int pandoraCore$getLastFrame();
	void pandoraCore$setLastTick(long tick);
	void pandoraCore$setLastFrame(int frame);
	
	long pandoraCore$accessGetCurrentTick();
	
	ImmutableList<Uniform> pandoraCore$getPerFrame();
	ImmutableList<Uniform> pandoraCore$getPerTick();
	ImmutableList<Uniform> pandoraCore$getDynamic();
}
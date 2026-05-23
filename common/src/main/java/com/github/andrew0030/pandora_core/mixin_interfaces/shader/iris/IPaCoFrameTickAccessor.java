package com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.uniform.Uniform;

public interface IPaCoFrameTickAccessor {
	long getLastTick();
	int getLastFrame();
	void setLastTick(long tick);
	void setLastFrame(int frame);
	
	long accessGetCurrentTick();
	
	ImmutableList<Uniform> getPerFrame();
	ImmutableList<Uniform> getPerTick();
	ImmutableList<Uniform> getDynamic();
}

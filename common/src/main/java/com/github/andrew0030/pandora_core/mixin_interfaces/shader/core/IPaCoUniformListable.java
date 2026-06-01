package com.github.andrew0030.pandora_core.mixin_interfaces.shader.core;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;

public interface IPaCoUniformListable extends IPaCoModTrackerListable{
    Iterable<AbstractUniform> pandoraCore$listUniforms();
	
	@Override
	default Iterable<IPaCoModTracker> pandoraCore$listTrackers() {
		return (Iterable<IPaCoModTracker>) (Object) pandoraCore$listUniforms();
	}
}
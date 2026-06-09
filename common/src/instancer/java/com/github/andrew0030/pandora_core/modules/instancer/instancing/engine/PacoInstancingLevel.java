package com.github.andrew0030.pandora_core.modules.instancer.instancing.engine;

public interface PacoInstancingLevel extends InstancingEnvironment {
	@Override
	default boolean isLevel() {
		return true;
	}
}

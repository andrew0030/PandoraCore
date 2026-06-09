package com.github.andrew0030.pandora_core.modules.instancer.instancing.engine;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface InstancingEnvironment {
	InstanceManager getManager();
	
	boolean isLevel();
	
	@Nullable Level getLevel();
}

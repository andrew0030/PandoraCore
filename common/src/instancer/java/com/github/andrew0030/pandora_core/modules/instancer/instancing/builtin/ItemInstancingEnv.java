package com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstanceManager;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemInstancingEnv implements InstancingEnvironment {
	InstanceManager manager;
	
	public ItemInstancingEnv(InstanceManager manager) {
		this.manager = manager;
	}
	
	@Override
	public InstanceManager getManager() {
		return manager;
	}
	
	@Override
	public boolean isLevel() {
		return false;
	}
	
	@Override
	public @Nullable Level getLevel() {
		return null;
	}
}

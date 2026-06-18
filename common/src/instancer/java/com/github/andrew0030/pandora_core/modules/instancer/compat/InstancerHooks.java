package com.github.andrew0030.pandora_core.modules.instancer.compat;

/**
 * This is a mixin target; if you are trying to setup some render state for your mod for compatibility reasons
 * This is the place to do it, just mixin to one of these methods
 */
public class InstancerHooks {
	public static void preStartInstancing() {
	}
	
	public static void postEndInstancing() {
	}
}

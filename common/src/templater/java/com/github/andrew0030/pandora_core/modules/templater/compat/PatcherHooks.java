package com.github.andrew0030.pandora_core.modules.templater.compat;

/**
 * This is a mixin target, if you need to override certain behaviors of the templater, this is the best place to do it
 */
public class PatcherHooks {
	public static boolean disableCustomCore() {
		return false;
	}
}

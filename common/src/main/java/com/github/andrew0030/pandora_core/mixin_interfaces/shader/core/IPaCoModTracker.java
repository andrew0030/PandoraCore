package com.github.andrew0030.pandora_core.mixin_interfaces.shader.core;

public interface IPaCoModTracker {
	/**
	 * @return the number of modifications tracked
	 */
	int pandoraCore$mod();
	
	boolean pandoraCore$needsModTracking();
	
	/**
	 * When a modification is needed to be tracked, this should be called
	 */
	void pandoraCore$enableModTracking();
	
	/**
	 * Marks dirty if the modification count does not match the expected modification count
	 *
	 * @param expectedMods the number of modifications expected
	 * @return whether or not it got marked dirty
	 */
	boolean pandoraCore$dirtyIfNotMod(int expectedMods);
	
	void pandoraCore$isolate();
	
	void pandoraCore$release();
}

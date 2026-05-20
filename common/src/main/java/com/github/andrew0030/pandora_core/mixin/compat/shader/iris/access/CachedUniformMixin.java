package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CachedUniform.class, remap = false)
public class CachedUniformMixin implements IPacoDirtyable, IPaCoModTracker {
	@Shadow
	private boolean changed;
	
	@Override
	public void pandoraCore$markDirty() {
		changed = true;
	}
	
	@Unique
	int pandoraCore$modCount = 0;
	@Unique
	boolean pandoraCore$shouldTrack = false;
	
	@Inject(at = @At("HEAD"), method = "update")
	public void preMarkDirty(CallbackInfo ci) {
		if (pandoraCore$shouldTrack) {
			pandoraCore$modCount++;
			pandoraCore$shouldTrack = false;
		}
	}
	
	@Override
	public int pandoraCore$mod() {
		return pandoraCore$modCount;
	}
	
	@Override
	public boolean pandoraCore$needsModTracking() {
		return pandoraCore$shouldTrack;
	}
	
	@Override
	public void pandoraCore$enableModTracking() {
		pandoraCore$shouldTrack = true;
	}
	
	@Override
	public boolean pandoraCore$dirtyIfNotMod(int expectedMods) {
		if (pandoraCore$modCount != expectedMods) {
			boolean st = pandoraCore$shouldTrack;
			// need to not increment
			pandoraCore$shouldTrack = false;
			changed = true;
			pandoraCore$shouldTrack = st;
			return true;
		}
		return false;
	}
}

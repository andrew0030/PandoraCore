package com.github.andrew0030.pandora_core.mixin.shader;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPacoDirtyable;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoModTracker;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Uniform.class)
public abstract class UniformMixin implements IPacoDirtyable, IPaCoModTracker {
	@Shadow
	protected abstract void markDirty();
	
	@Shadow
	private boolean dirty;
	
	@Override
	public void pandoraCore$markDirty() {
		markDirty();
	}
	
	@Unique
	int pandoraCore$modCount = 0;
	@Unique
	boolean pandoraCore$shouldTrack = false;
	@Unique
	boolean pandoraCore$wasDirty = false;
	@Unique
	boolean pandoraCore$changed = false;
	
	@Inject(at = @At("HEAD"), method = "markDirty")
	public void preMarkDirty(CallbackInfo ci) {
		pandoraCore$changed = true;
		if (pandoraCore$shouldTrack) {
			pandoraCore$modCount++;
			pandoraCore$shouldTrack = false;
		}
	}
	
	@Override
	public void pandoraCore$isolate() {
		pandoraCore$changed = false;
		pandoraCore$wasDirty = dirty;
	}
	
	@Override
	public void pandoraCore$release() {
		dirty = pandoraCore$wasDirty || pandoraCore$changed;
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
			markDirty();
			pandoraCore$shouldTrack = st;
			return true;
		}
		return false;
	}
}

package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoFrameTickAccessor;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPacoInitCachable;
import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProgramUniforms.class, remap = false)
public abstract class ProgramUniformsMixin implements IPacoInitCachable<ImmutableList<Uniform>>, IPaCoFrameTickAccessor {
    @Shadow private ImmutableList<Uniform> once;
	@Shadow long lastTick;
	@Shadow int lastFrame;
	@Shadow @Final private ImmutableList<Uniform> perFrame;
	@Shadow @Final private ImmutableList<Uniform> perTick;
	@Shadow @Final private ImmutableList<Uniform> dynamic;
	@Shadow private static long getCurrentTick() {
		throw new RuntimeException("");
	}
	@Unique private ImmutableList<Uniform> pandoraCore$cacheOnce;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(ImmutableList once, ImmutableList perTick, ImmutableList perFrame, ImmutableList dynamic, ImmutableList notifiersToReset, CallbackInfo ci) {
		pandoraCore$cacheOnce = once;
	}
	
    @Override
    public ImmutableList<Uniform> pandoraCore$getInitializer() {
        return pandoraCore$cacheOnce;
    }
	
	@Override
	public ImmutableList<Uniform> pandoraCore$getCurrentInitializer() {
		return once;
	}
	
	@Override
    public void pandoraCore$setInitializer(ImmutableList<Uniform> initializer) {
        once = initializer;
    }
	
	@Override
	public long pandoraCore$getLastTick() {
		return lastTick;
	}
	
	@Override
	public int pandoraCore$getLastFrame() {
		return lastFrame;
	}
	
	@Override
	public void pandoraCore$setLastTick(long tick) {
		lastTick = tick;
	}
	
	@Override
	public void pandoraCore$setLastFrame(int frame) {
		lastFrame = frame;
	}
	
	@Override
	public ImmutableList<Uniform> pandoraCore$getPerFrame() {
		return perFrame;
	}
	
	@Override
	public ImmutableList<Uniform> pandoraCore$getPerTick() {
		return perTick;
	}
	
	@Override
	public ImmutableList<Uniform> pandoraCore$getDynamic() {
		return dynamic;
	}
	
	@Override
	public long pandoraCore$accessGetCurrentTick() {
		return getCurrentTick();
	}
}
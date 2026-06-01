package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.modules.templater.itf.ILocationedObject;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShaderUniformBase.class, remap = false)
public abstract class ShaderUBaseAccessor implements ILocationedObject {
	@Shadow
	public abstract int getLocation();
	
	@Shadow
	private int[] locations;
	@Shadow
	private int program;
	@Unique
	int pandoraCore$vLoc = -1;
	@Unique
	int pandoraCore$rLoc = 0;
	
	@Override
	public void pandoraCore$virtualLocation(int location) {
		pandoraCore$rLoc = getLocation();
		pandoraCore$vLoc = location;
//		locations[program] = pandoraCore$vLoc;
	}
	
	@Override
	public int pandoraCore$getRealLocation() {
//		return locations[program];
		return pandoraCore$rLoc;
	}
	
	@Inject(at = @At("HEAD"), method = "getLocation", cancellable = true)
	public void preGetLoc(CallbackInfoReturnable<Integer> cir) {
		if (pandoraCore$vLoc > 0) {
			cir.setReturnValue(pandoraCore$vLoc);
		}
	}
}

package com.github.andrew0030.pandora_core.modules.templater.mixin.vanilla.access;

import com.github.andrew0030.pandora_core.modules.templater.itf.ILocationedObject;
import com.github.andrew0030.pandora_core.modules.templater.itf.INamedUniform;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Uniform.class)
public abstract class UniformMixin implements INamedUniform, ILocationedObject {
	@Shadow
	public abstract String getName();
	
	@Shadow
	private int location;
	@Unique
	int rloc;
	@Unique
	int vloc = -1;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(String name, int type, int count, Shader parent, CallbackInfo ci) {
		rloc = location;
	}
	
	@Inject(at = @At("RETURN"), method = "setLocation")
	public void postSetLoc(int location, CallbackInfo ci) {
		this.rloc = location;
	}
	
	@Override
	public String pandoraCore$getName() {
		return getName();
	}
	
	
	@Override
	public void pandoraCore$virtualLocation(int location) {
		vloc = location;
		if (vloc != -1)
			this.location = vloc;
		else
			this.location = rloc;
	}
	
	@Override
	public int pandoraCore$getRealLocation() {
		return location;
	}
	
	@Inject(at = @At("HEAD"), method = "getLocation", cancellable = true)
	public void preGetLoc(CallbackInfoReturnable<Integer> cir) {
		if (vloc != -1)
			cir.setReturnValue(vloc);
	}
}

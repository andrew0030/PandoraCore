package com.github.andrew0030.pandora_core.modules.templater.mixin.vanilla.access;

import com.github.andrew0030.pandora_core.modules.templater.itf.ILocationedObject;
import com.github.andrew0030.pandora_core.modules.templater.itf.INamedUniform;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractUniform.class)
public abstract class AbstractUniformMixin implements INamedUniform, ILocationedObject {
	@Override
	public String pandoraCore$getName() {
		return null;
	}
	
	@Override
	public void pandoraCore$virtualLocation(int location) {
		// no-op
	}
	
	@Override
	public int pandoraCore$getRealLocation() {
		return -1;
	}
}

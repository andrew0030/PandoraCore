package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.modules.templater.itf.ILocationedObject;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShaderUniformBase.class)
public abstract class ShaderUBaseAccessor implements ILocationedObject {
	@Shadow
	public abstract int getLocation();
	
	@Shadow
	private int[] locations;
	@Shadow
	private int program;
	@Unique
	int pandoraCore$vLoc = 0;
	
	@Override
	public void pandoraCore$virtualLocation(int location) {
		locations[program] = pandoraCore$vLoc;
	}
	
	@Override
	public int pandoraCore$getRealLocation() {
		return getLocation();
	}
}

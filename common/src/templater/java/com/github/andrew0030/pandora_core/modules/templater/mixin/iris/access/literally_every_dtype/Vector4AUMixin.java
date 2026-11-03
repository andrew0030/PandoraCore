package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector4ArrayUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Vector4ArrayUniform.class, remap = false)
public class Vector4AUMixin implements IPaCoPainReducer {
	@Shadow private float[] cachedValue;
	
	@Override
	public Object pandoraCore$getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void pandoraCore$setCachedValue(Object object) {
		cachedValue = (float[]) object;
	}
}
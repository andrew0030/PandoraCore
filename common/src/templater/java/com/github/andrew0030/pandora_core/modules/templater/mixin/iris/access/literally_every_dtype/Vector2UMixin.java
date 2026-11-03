package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector2Uniform;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Vector2Uniform.class, remap = false)
public class Vector2UMixin implements IPaCoPainReducer {
	@Shadow private Vector2f cachedValue;
	
	@Override
	public Object pandoraCore$getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void pandoraCore$setCachedValue(Object object) {
		cachedValue = (Vector2f) object;
	}
}
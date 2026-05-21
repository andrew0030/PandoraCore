package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector3IntegerUniform;
import net.irisshaders.iris.gl.uniform.Vector3Uniform;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector3IntegerUniform.class)
public class Vector3IUMixin implements IPaCoPainReducer {
	@Shadow
	private Vector3i cachedValue;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		cachedValue = (Vector3i) object;
	}
}

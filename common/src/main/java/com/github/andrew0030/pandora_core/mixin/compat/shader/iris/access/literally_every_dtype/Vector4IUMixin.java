package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector4IntegerJomlUniform;
import net.irisshaders.iris.gl.uniform.Vector4Uniform;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector4IntegerJomlUniform.class)
public class Vector4IUMixin implements IPaCoPainReducer {
	@Shadow
	private Vector4i cachedValue;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		cachedValue = (Vector4i) object;
	}
}

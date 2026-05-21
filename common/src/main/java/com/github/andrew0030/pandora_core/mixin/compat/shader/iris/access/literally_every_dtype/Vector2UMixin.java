package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.MatrixFromFloatArrayUniform;
import net.irisshaders.iris.gl.uniform.Vector2Uniform;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector2Uniform.class)
public class Vector2UMixin implements IPaCoPainReducer {
	@Shadow
	private Vector2f cachedValue;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		cachedValue = (Vector2f) object;
	}
}

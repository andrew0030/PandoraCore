package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector3IntegerUniform;
import net.irisshaders.iris.gl.uniform.Vector4Uniform;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector4Uniform.class)
public class Vector4UMixin implements IPaCoPainReducer {
	@Final
	@Mutable
	@Shadow
	private Vector4f cachedValue;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		if (object == null) {
			cachedValue = new Vector4f();
		} else {
			cachedValue = (Vector4f) object;
		}
	}
}

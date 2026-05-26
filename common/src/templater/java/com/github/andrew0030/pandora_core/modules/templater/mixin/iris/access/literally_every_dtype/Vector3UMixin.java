package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector3Uniform;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector3Uniform.class)
public class Vector3UMixin implements IPaCoPainReducer {
	@Final
	@Mutable
	@Shadow
	private Vector3f cachedValue;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		if (object == null) {
			cachedValue = new Vector3f();
		} else {
			cachedValue = (Vector3f) object;
		}
	}
}

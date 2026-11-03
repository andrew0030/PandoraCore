package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.Vector2IntegerJomlUniform;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Vector2IntegerJomlUniform.class, remap = false)
public class Vector2IUMixin implements IPaCoPainReducer {
	@Shadow private Vector2i cachedValue;
	
	@Override
	public Object pandoraCore$getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void pandoraCore$setCachedValue(Object object) {
		cachedValue = (Vector2i) object;
	}
}
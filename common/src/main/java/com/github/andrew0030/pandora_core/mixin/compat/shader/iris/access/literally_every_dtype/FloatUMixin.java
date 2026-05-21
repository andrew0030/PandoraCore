package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.FloatUniform;
import net.irisshaders.iris.gl.uniform.MatrixUniform;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FloatUniform.class, remap = false)
public class FloatUMixin implements IPaCoPainReducer {
	@Shadow
	private float cachedValue;
	@Shadow
	@Final
	private FloatSupplier value;
	private boolean cacheNulled = false;
	
	@Override
	public Object getCachedValue() {
		return cachedValue;
	}
	
	@Override
	public void setCachedValue(Object object) {
		if (object == null) {
			cacheNulled = true;
		} else {
			cachedValue = (float) object;
		}
	}
	
	// TODO: temp
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	private void updateValue() {
		float newValue = this.value.getAsFloat();
		if (cacheNulled || this.cachedValue != newValue) {
			this.cachedValue = newValue;
			IrisRenderSystem.uniform1f(((Uniform) (Object) this).getLocation(), newValue);
		}
		
	}
}


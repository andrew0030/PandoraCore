package com.github.andrew0030.pandora_core.mixin.compat.shader.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.FloatUniform;
import net.irisshaders.iris.gl.uniform.IntUniform;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.IntSupplier;

@Mixin(value = IntUniform.class, remap = false)
public class IntUMixin implements IPaCoPainReducer {
	@Shadow
	private int cachedValue;
	@Shadow
	@Final
	private IntSupplier value;
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
			cachedValue = (int) object;
		}
	}
	
	// TODO: temp
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	private void updateValue() {
		int newValue = this.value.getAsInt();
		if (cacheNulled || this.cachedValue != newValue) {
			this.cachedValue = newValue;
			RenderSystem.glUniform1i(((Uniform) (Object) this).getLocation(), newValue);
		}
		
	}
}

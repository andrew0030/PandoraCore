package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.FloatUniform;
import net.irisshaders.iris.gl.uniform.Uniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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
			cacheNulled = false;
			cachedValue = (float) object;
		}
	}
	
	@Definition(id = "cachedValue", field = "Lnet/irisshaders/iris/gl/uniform/FloatUniform;cachedValue:F")
	@Definition(id = "newValue", local = @Local(type = float.class, ordinal = 0))
	@Expression("this.cachedValue != newValue")
	@ModifyExpressionValue(
			method = "updateValue",
			at = @At(value = "MIXINEXTRAS:EXPRESSION")
	)
	private boolean updateValue(boolean original) {
		return cacheNulled || original;
	}
}


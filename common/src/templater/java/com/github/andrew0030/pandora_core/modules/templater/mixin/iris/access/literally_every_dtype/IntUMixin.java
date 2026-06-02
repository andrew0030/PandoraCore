package com.github.andrew0030.pandora_core.modules.templater.mixin.iris.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.At;

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
	
	@Definition(id = "cachedValue", field = "Lnet/irisshaders/iris/gl/uniform/IntUniform;cachedValue:I")
	@Definition(id = "newValue", local = @Local(type = int.class, ordinal = 0))
	@Expression("this.cachedValue != newValue")
	@ModifyExpressionValue(
			method = "updateValue",
			at = @At(value = "MIXINEXTRAS:EXPRESSION")
	)
	private boolean updateValue(boolean original) {
		return cacheNulled || original;
	}
}

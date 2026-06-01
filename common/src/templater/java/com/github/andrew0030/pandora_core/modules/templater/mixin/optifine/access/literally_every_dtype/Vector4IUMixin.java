package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.irisshaders.iris.gl.uniform.Vector4IntegerJomlUniform;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.joml.Vector4i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShaderUniform4i.class, remap = false)
public class Vector4IUMixin implements IPaCoPainReducer {
	@Shadow
	private int[][] programValues;
	private boolean cacheNulled = false;
	
	@Override
	public Object getCachedValue() {
		return programValues[((ShaderUniformBase) (Object) this).getProgram()];
	}
	
	@Override
	public void setCachedValue(Object object) {
		int[] obj = (int[]) object;
		if (object == null) {
			cacheNulled = true;
			programValues[((ShaderUniformBase) (Object) this).getProgram()] = new int[4];
		} else {
			programValues[((ShaderUniformBase) (Object) this).getProgram()] = obj;
		}
	}
	
	@Definition(id = "x", local = @Local(type = int.class, ordinal = 0))
	@Definition(id = "valueOld", local = @Local(type = int[].class, ordinal = 0))
	@Expression("valueOld[0] == x")
	@ModifyExpressionValue(
			method = "setValue",
			at = @At(value = "MIXINEXTRAS:EXPRESSION")
	)
	private boolean updateValue(boolean original) {
		return cacheNulled || original;
	}
}

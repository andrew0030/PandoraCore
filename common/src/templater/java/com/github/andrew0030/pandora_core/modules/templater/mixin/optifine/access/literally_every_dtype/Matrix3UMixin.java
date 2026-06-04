package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access.literally_every_dtype;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import net.irisshaders.iris.gl.uniform.MatrixFromFloatArrayUniform;
import net.optifine.shaders.uniform.ShaderUniformM3;
import org.joml.Matrix3f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.FloatBuffer;

@Mixin(ShaderUniformM3.class)
public class Matrix3UMixin implements IPaCoPainReducer {
	@Shadow
	private FloatBuffer matrixBuffer;
	
	@Override
	public Object getCachedValue() {
		return matrixBuffer;
	}
	
	@Override
	public void setCachedValue(Object object) {
//		if (object == null) {
//			matrixBuffer = MemoryUtil.memAllocFloat(9);
//		} else {
//			matrixBuffer = (FloatBuffer) object;
//		}
	}
}

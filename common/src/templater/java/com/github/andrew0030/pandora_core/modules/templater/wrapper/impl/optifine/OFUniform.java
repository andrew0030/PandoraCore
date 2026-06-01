package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.iris.IPaCoPainReducer;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class OFUniform extends AbstractUniform {
	ShaderUniformBase base;
	IPaCoPainReducer pr;
	
	public OFUniform(ShaderUniformBase base) {
		this.base = base;
		pr = (IPaCoPainReducer) base;
	}
	
	@Override
	public void set(float x) {
		pr.setCachedValue(x);
	}
	
	@Override
	public void set(float x, float y) {
		pr.setCachedValue(new float[]{x, y});
	}
	
	@Override
	public void set(float x, float y, float z) {
		pr.setCachedValue(new float[]{x, y, z});
	}
	
	@Override
	public void set(float x, float y, float z, float w) {
		pr.setCachedValue(new float[]{x, y, z, w});
	}
	
	@Override
	public void setSafe(float x, float y, float z, float w) {
		throw new RuntimeException("Unsupported.");
	}
	
	@Override
	public void setSafe(int x, int y, int z, int w) {
		throw new RuntimeException("Unsupported.");
	}
	
	@Override
	public void set(int x) {
		pr.setCachedValue(x);
	}
	
	@Override
	public void set(int x, int y) {
		pr.setCachedValue(new int[]{x, y});
	}
	
	@Override
	public void set(int x, int y, int z) {
		pr.setCachedValue(new int[]{x, y, z});
	}
	
	@Override
	public void set(int x, int y, int z, int w) {
		pr.setCachedValue(new int[]{x, y, z, w});
	}
	
	@Override
	public void set(float[] valueArray) {
		pr.setCachedValue(valueArray);
	}
	
	@Override
	public void set(Vector3f vector) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void set(Vector4f vector) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat2x2(float m00, float m01, float m10, float m11) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat2x3(float m00, float m01, float m02, float m10, float m11, float m12) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat2x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat3x2(float m00, float m01, float m10, float m11, float m20, float m21) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat3x3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat3x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat4x2(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat4x3(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void setMat4x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void set(Matrix4f matrix) {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public void set(Matrix3f matrix) {
		throw new RuntimeException("TODO");
	}
}

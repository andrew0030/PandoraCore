package com.github.andrew0030.pandora_core.client.render.optifine;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleBufferBuilder;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Mth;
import net.optifine.shaders.SVertexBuilder;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TriangularSVB extends SVertexBuilder implements PaCoSVB {
	Vector3f v0 = new Vector3f();
	Vector3f v1 = new Vector3f();
	Vector3f v2 = new Vector3f();
	Vector3f i0 = new Vector3f();
	Vector3f i1 = new Vector3f();
	Vector3f tan0 = new Vector3f();
	Vector3f tan1 = new Vector3f();
	
	@Override
	public void pandoraCore$endAddVertex(BufferBuilder builder) {
		if (!(!ShaderChecker.isShaderActive() || !OptifineAccessor.isItemRendering()))
			return;
		
		SVertexBuilder svb = (SVertexBuilder) OptifineAccessor.getSVB(builder);
		
		int vertSize = OptifineAccessor.getVertexSize(svb);
		
		if (vertSize != 18)
			return;
		
		IPaCoAccessibleBufferBuilder bb = (IPaCoAccessibleBufferBuilder) builder;
//		System.out.println("Vert: " + bb.pandoraCore$getVertexCount());
		if (bb.pandoraCore$getVertexCount() % bb.pandoraCore$getDrawMode().primitiveStride != 0)
			return;
//		System.out.println("TANGENT");
		
		int size = OptifineAccessor.getIntSize(builder);
		this.calcTangent(builder, size - 2 * vertSize);
		
		IntBuffer ib = OptifineAccessor.getIntBuffer(builder);
		
		long[] edat = OptifineAccessor.getEntityData(svb);
		int idx = OptifineAccessor.getEntityDatIndex(svb);
		long eData = edat[idx];
		int pos = size - 18 + 13;
		
		pos += OptifineAccessor.getIntStartPos(builder);
		ib.put(pos, (int) eData);
		ib.put(pos + 1, (int) (eData >> 32));
	}
	
	@Override
	public void pandoraCore$endAddVertexData(BufferBuilder builder) {
		IPaCoAccessibleBufferBuilder bb = (IPaCoAccessibleBufferBuilder) builder;
		if (bb.pandoraCore$getVertexCount() % bb.pandoraCore$getDrawMode().primitiveStride != 0)
			return;
		
		SVertexBuilder svb = (SVertexBuilder) OptifineAccessor.getSVB(builder);
		int vertSize = OptifineAccessor.getVertexSize(svb);
		int size = OptifineAccessor.getIntSize(builder);
		this.calcTangent(builder, size - 2 * vertSize);
	}
	
	float normFrom(float value) {
		if (value == 0) return 1.0f;
		return 1 / Mth.sqrt(value);
	}
	
	private void normFrom(
			Vector3f tan0,
			float v2, float v1,
			Vector3f vector0, Vector3f vector1,
			float r, boolean normalize
	) {
		tan0.set(
				(v2 * vector0.x() - v1 * vector1.x()) * r,
				(v2 * vector0.y() - v1 * vector1.y()) * r,
				(v2 * vector0.z() - v1 * vector1.z()) * r
		);
		
		if (normalize) {
			float mult = tan0.length();
			tan0.mul(normFrom(mult));
		}
	}
	
	private void calcTangent(BufferBuilder builder, int baseIndex) {
		SVertexBuilder svb = (SVertexBuilder) OptifineAccessor.getSVB(builder);
		int vertSize = OptifineAccessor.getVertexSize(svb);

		IntBuffer ib = OptifineAccessor.getIntBuffer(builder);
		FloatBuffer fb = OptifineAccessor.getFloatBuffer(builder);
		baseIndex += OptifineAccessor.getIntStartPos(builder);

		int offsetUV = OptifineAccessor.getUVOffset(svb);
		int offsetNormal = OptifineAccessor.getNormOffset(svb);

		v0.set(
				fb.get(baseIndex + 0 * vertSize),
				fb.get(baseIndex + 0 * vertSize + 1),
				fb.get(baseIndex + 0 * vertSize + 2)
		);
		float v0u = fb.get(baseIndex + 0 * vertSize + offsetUV);
		float v0v = fb.get(baseIndex + 0 * vertSize + offsetUV + 1);
		v1.set(
				fb.get(baseIndex + 1 * vertSize),
				fb.get(baseIndex + 1 * vertSize + 1),
				fb.get(baseIndex + 1 * vertSize + 2)
		);
		float v1u = fb.get(baseIndex + 1 * vertSize + offsetUV);
		float v1v = fb.get(baseIndex + 1 * vertSize + offsetUV + 1);
		v2.set(
				fb.get(baseIndex + 2 * vertSize),
				fb.get(baseIndex + 2 * vertSize + 1),
				fb.get(baseIndex + 2 * vertSize + 2)
		);
		float v2u = fb.get(baseIndex + 2 * vertSize + offsetUV);
		float v2v = fb.get(baseIndex + 2 * vertSize + offsetUV + 1);

		float mult;
		
		i0.set(v1).sub(v0);
		i1.set(v2).sub(v0);
		
		float u_1 = v1u - v0u;
		float _v1 = v1v - v0v;
		float u_2 = v2u - v0u;
		float _v2 = v2v - v0v;
		float d = u_1 * _v2 - u_2 * _v1;
		float r = d != 0.0F ? 1.0F / d : 1.0F;
		
		normFrom(
				tan0,
				_v2, _v1,
				this.i0, this.i1,
				r, true
		);
		normFrom(
				tan1,
				u_2, u_1,
				this.i0, this.i1,
				r, true
		);
		
		for (int i = 0; i < 3; i++) {
			int packedNormal = ib.get(baseIndex + i * vertSize + offsetNormal);
			byte bnx = (byte) (packedNormal & 0XFF);
			byte bunny = (byte) ((packedNormal >> 8) & 0XFF);
			byte bnz = (byte) ((packedNormal >> 16) & 0XFF);
			
			float vnx = bnx / 127f;
			float vny = bunny / 127f;
			float vnz = bnz / 127f;
			
			mult = normFrom(vnx * vnx + vny * vny + vnz * vnz);
			vnx *= mult;
			vny *= mult;
			vnz *= mult;

			float tan3x = vnz * tan0.y() - vny * tan0.z();
			float tan3y = vnx * tan0.z() - vnz * tan0.x();
			float tan3z = vny * tan0.x() - vnx * tan0.y();
			float tan1w = tan1.x() * tan3x + tan1.y() * tan3y + tan1.z() * tan3z < 0.0F ? -1.0F : 1.0F;
			
			int packedTan1xy = ((int) (tan0.x() * 32767.0F) & 0xFFFF) + (((int) (tan0.y() * 32767.0F) & 0xFFFF) << 16);
			int packedTan1zw = ((int) (tan0.z() * 32767.0F) & 0xFFFF) + (((int) (tan1w * 32767.0F) & 0xFFFF) << 16);
			ib.put(baseIndex + i * vertSize + 11, packedTan1xy);
			ib.put(baseIndex + i * vertSize + 11 + 1, packedTan1zw);
		}

		// TODO: probably utilize barycentric coordinates to extrapolate the middle of a square texture?
		float midU = (v0u + v1u + v2u) / 3.0F;
		float midV = (v0v + v1v + v2v) / 3.0F;
		fb.put(baseIndex + 0 * vertSize + 9, midU);
		fb.put(baseIndex + 0 * vertSize + 9 + 1, midV);
		fb.put(baseIndex + 1 * vertSize + 9, midU);
		fb.put(baseIndex + 1 * vertSize + 9 + 1, midV);
		fb.put(baseIndex + 2 * vertSize + 9, midU);
		fb.put(baseIndex + 2 * vertSize + 9 + 1, midV);
	}
}

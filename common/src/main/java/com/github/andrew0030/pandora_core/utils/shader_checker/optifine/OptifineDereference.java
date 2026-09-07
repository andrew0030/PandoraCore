package com.github.andrew0030.pandora_core.utils.shader_checker.optifine;

import com.github.andrew0030.pandora_core.client.render.optifine.TriangularSVB;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleBufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor.*;

public class OptifineDereference {
	public static void debug(BufferBuilder builder) {
		Object svertBuilder = svb.get(theUnsafe, builder, Object.class);
		if (svertBuilder == null) return;
		
		System.out.println("== OPTIFINE SHADER VERTEX BUILDER ==");
		System.out.println("Draw Mode: " + ((IPaCoAccessibleBufferBuilder) builder).pandoraCore$getDrawMode());
		System.out.println("Vertex Size: " + svbVertexSize.getPrimitive(theUnsafe, svertBuilder, int.class));
		System.out.println("Normals: " + svbHasNormal.getPrimitive(theUnsafe, svertBuilder, boolean.class));
		System.out.println("Tangents: " + svbHasTangent.getPrimitive(theUnsafe, svertBuilder, boolean.class));
	}
	
	public static int getVertexSize(SVertexBuilder svertBuilder) {
		return svbVertexSize.getPrimitive(theUnsafe, svertBuilder, int.class);
	}
	
	public static SVertexBuilder getSVB(BufferBuilder builder) {
		return (SVertexBuilder) svb.get(theUnsafe, builder, Object.class);
	}
	
	public static IntBuffer getIntBuffer(BufferBuilder builder) {
		return vbIntBuffer.get(theUnsafe, builder, IntBuffer.class);
	}
	
	public static FloatBuffer getFloatBuffer(BufferBuilder builder) {
		return vbFloatBuffer.get(theUnsafe, builder, FloatBuffer.class);
	}
	
	public static int getIntSize(BufferBuilder builder) {
		IPaCoAccessibleBufferBuilder bb = (IPaCoAccessibleBufferBuilder) builder;
		return bb.pandoraCore$getVertexCount() * ((IPaCoAccessibleBufferBuilder) builder).pandoraCore$getFormat().getIntegerSize();
	}
	
	public static int getIntStartPos(BufferBuilder builder) {
		IPaCoAccessibleBufferBuilder bb = (IPaCoAccessibleBufferBuilder) builder;
		return bb.pandoraCore$getRenderedBufferPointer() / 4;
	}
	
	public static long[] getEntityData(SVertexBuilder svb) {
		return svbEntityDat.get(theUnsafe, svb, long[].class);
	}
	
	public static int getEntityDatIndex(SVertexBuilder svb) {
		return svbEntityDatIdx.getPrimitive(theUnsafe, svb, int.class);
	}
	
	public static int getUVOffset(SVertexBuilder svb) {
		return svbUVOff.getPrimitive(theUnsafe, svb, int.class);
	}
	
	public static int getNormOffset(SVertexBuilder svb) {
		return svbNormOff.getPrimitive(theUnsafe, svb, int.class);
	}
	
	public static void setSVB(BufferBuilder builder, SVertexBuilder triangularSVB) {
		svb.set(theUnsafe, builder, triangularSVB);
	}
	
	public static CustomUniform[] getUniformList(CustomUniforms uniforms) {
		if (uniforms == null) return new CustomUniform[0];
		return CUSTOM_UNIFORMS_LIST.get(theUnsafe, uniforms, CustomUniform[].class);
	}
	
	public static void prepareSVB(BufferBuilder builder, VertexFormat.Mode mode, VertexFormat format) {
		if (mode == VertexFormat.Mode.TRIANGLES) {
			setSVB(builder, new TriangularSVB());
		} else {
			setSVB(builder, new SVertexBuilder());
		}
	}
}

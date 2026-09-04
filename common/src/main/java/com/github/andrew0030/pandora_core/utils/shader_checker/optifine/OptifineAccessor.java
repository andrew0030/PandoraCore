package com.github.andrew0030.pandora_core.utils.shader_checker.optifine;

import com.github.andrew0030.pandora_core.client.render.optifine.TriangularSVB;
import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleBufferBuilder;
import com.github.andrew0030.pandora_core.utils.unsafe.FieldAccessor;
import com.github.andrew0030.pandora_core.utils.unsafe.TheUnsafeHelper;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.optifine.shaders.Program;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.util.WorldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class OptifineAccessor {
	public static boolean FALSE_BIND = false;
	public static final boolean optifinePresent;
	
	private static FieldAccessor svb;
	private static FieldAccessor svbVertexSize;
	private static FieldAccessor svbHasNormal;
	private static FieldAccessor svbHasTangent;
	
	private static FieldAccessor svbEntityDat;
	private static FieldAccessor svbEntityDatIdx;
	private static FieldAccessor svbUVOff;
	private static FieldAccessor svbNormOff;
	
	private static FieldAccessor vbIntBuffer;
	private static FieldAccessor vbFloatBuffer;
	
	private static FieldAccessor program;
	private static FieldAccessor currentWorld;
	
	private static FieldAccessor BLOCK_VANILLA;
	private static FieldAccessor BLOCK_SHADERS;
	private static FieldAccessor ENTITY_VANILLA;
	private static FieldAccessor ENTITY_SHADERS;
	
	private static FieldAccessor CUSTOM_UNIFORMS;
	private static FieldAccessor SHADER_UNIFORMS;
	private static FieldAccessor CUSTOM_UNIFORMS_LIST;
	
	private static Method checkGlError;
	private static Method bindGbuffersTextures;

	private static Method isRenderItemGui;
	private static Method setRenderItemGui;
	
	private static final Unsafe theUnsafe;
	
	static {
		theUnsafe = TheUnsafeHelper.getTheUnsafe();
		
		boolean presence = false;
		
		FieldAccessor prog = null;
		if (ShaderChecker.OF_HANDLER.isLoaded()) {
			presence = true;
			
			prog = new FieldAccessor(theUnsafe, GlStateManager.class, "glProgram");
			currentWorld = new FieldAccessor(theUnsafe, Shaders.class, "currentWorld");
			
			BLOCK_VANILLA = new FieldAccessor(theUnsafe, DefaultVertexFormat.class, "BLOCK_VANILLA");
			BLOCK_SHADERS = new FieldAccessor(theUnsafe, DefaultVertexFormat.class, "BLOCK_SHADERS");
			ENTITY_VANILLA = new FieldAccessor(theUnsafe, DefaultVertexFormat.class, "ENTITY_VANILLA");
			ENTITY_SHADERS = new FieldAccessor(theUnsafe, DefaultVertexFormat.class, "ENTITY_SHADERS");
			
			CUSTOM_UNIFORMS = new FieldAccessor(theUnsafe, Shaders.class, "customUniforms");
			CUSTOM_UNIFORMS_LIST = new FieldAccessor(theUnsafe, CustomUniforms.class, "uniforms");
			SHADER_UNIFORMS = new FieldAccessor(theUnsafe, Shaders.class, "shaderUniforms");
			
			try {
				// TODO: method handle
				checkGlError = Shaders.class.getDeclaredMethod("checkGLError", String.class);
				checkGlError.setAccessible(true);
				
				bindGbuffersTextures = Shaders.class.getDeclaredMethod("bindGbuffersTextures");
				bindGbuffersTextures.setAccessible(true);
				
				isRenderItemGui = ItemRenderer.class.getDeclaredMethod("isRenderItemGui");
				isRenderItemGui.setAccessible(true);
				
				setRenderItemGui = ItemRenderer.class.getDeclaredMethod("setRenderItemGui", boolean.class);
				setRenderItemGui.setAccessible(true);
			} catch (Throwable err) {
				err.printStackTrace();
				throw new RuntimeException(err);
			}
			
			try {
				svb = new FieldAccessor(theUnsafe, BufferBuilder.class, "sVertexBuilder");
				svbVertexSize = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "vertexSize");
				svbHasNormal = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "hasNormal");
				svbHasTangent = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "hasTangent");
				svbUVOff = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "offsetUV");
				svbNormOff = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "offsetNormal");
				
				svbEntityDat = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "entityData");
				svbEntityDatIdx = new FieldAccessor(theUnsafe, Class.forName("net.optifine.shaders.SVertexBuilder"), "entityDataIndex");
				
				vbIntBuffer = new FieldAccessor(theUnsafe, BufferBuilder.class, "intBuffer");
				vbFloatBuffer = new FieldAccessor(theUnsafe, BufferBuilder.class, "floatBuffer");
			} catch (Throwable err) {
				err.printStackTrace();
				throw new RuntimeException(err);
			}
		}
		
		optifinePresent = presence;
		program = prog;
	}
	
	public static void lieToOFAboutProgram(int id) {
		if (optifinePresent) {
			theUnsafe.putInt(program.base, program.offset, id);
		}
	}
	
	public static int getActiveWorldID() {
		return WorldUtils.getDimensionId(
				currentWorld.get(theUnsafe, ClientLevel.class)
		);
	}
	
	public static String dimensionShader() {
		return "/shaders/world" + getActiveWorldID() + "/";
	}
	
	public static void falseBind(Program from) {
//		FALSE_BIND = true;
//		Shaders.useProgram(from);
//		FALSE_BIND = false;
		Shaders.activeProgram = from;
		Shaders.activeProgramID = from.getId();
		getShaderUniforms().setProgram(from.getId());
		CustomUniforms cuforms = getCustomUniforms();
		if (cuforms != null)
			cuforms.setProgram(from.getId());
	}
	
	public static void falseUnbind() {
//		Shaders.activeProgram = Shaders.ProgramNone;
//		Shaders.activeProgramID = Shaders.ProgramNone.getId();
		FALSE_BIND = true;
		Shaders.useProgram(Shaders.ProgramNone);
		FALSE_BIND = false;
	}
	
	public static VertexFormat getEntityVanilla() {
		return ENTITY_VANILLA.get(theUnsafe, VertexFormat.class);
	}
	
	public static VertexFormat getEntityShader() {
		return ENTITY_SHADERS.get(theUnsafe, VertexFormat.class);
	}
	
	public static VertexFormat getBlockVanilla() {
		return BLOCK_VANILLA.get(theUnsafe, VertexFormat.class);
	}
	
	public static VertexFormat getBlockShader() {
		return BLOCK_SHADERS.get(theUnsafe, VertexFormat.class);
	}
	
	public static CustomUniforms getCustomUniforms() {
		return CUSTOM_UNIFORMS.get(theUnsafe, CustomUniforms.class);
	}
	
	public static ShaderUniforms getShaderUniforms() {
		return SHADER_UNIFORMS.get(theUnsafe, ShaderUniforms.class);
	}
	
	public static void checkGlError(String label) {
		try {
			checkGlError.invoke(null, label);
		} catch (Throwable err) {
		}
	}
	
	public static void bindGbuffersTextures() {
		try {
			bindGbuffersTextures.invoke(null);
		} catch (Throwable err) {
		}
	}
	
	public static boolean isItemRendering() {
		try {
			return (boolean) isRenderItemGui.invoke(null);
		} catch (IllegalAccessException | InvocationTargetException e) {
			return false;
		}
	}
	
	public static void setItemRendering(boolean value) {
		try {
			setRenderItemGui.invoke(null, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
		}
	}
	
	public static void debug(BufferBuilder builder) {
		Object svertBuilder = svb.get(theUnsafe, builder, Object.class);
		if (svertBuilder == null) return;
		
		System.out.println("== OPTIFINE SHADER VERTEX BUILDER ==");
		System.out.println("Draw Mode: " + ((IPaCoAccessibleBufferBuilder) builder).pandoraCore$getDrawMode());
		System.out.println("Vertex Size: " + svbVertexSize.getPrimitive(theUnsafe, svertBuilder, int.class));
		System.out.println("Normals: " + svbHasNormal.getPrimitive(theUnsafe, svertBuilder, boolean.class));
		System.out.println("Tangents: " + svbHasTangent.getPrimitive(theUnsafe, svertBuilder, boolean.class));
	}
	
	public static int getVertexSize(Object svertBuilder) {
		return svbVertexSize.getPrimitive(theUnsafe, svertBuilder, int.class);
	}
	
	public static Object getSVB(BufferBuilder builder) {
		return svb.get(theUnsafe, builder, Object.class);
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

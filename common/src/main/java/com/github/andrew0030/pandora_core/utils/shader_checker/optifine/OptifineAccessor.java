package com.github.andrew0030.pandora_core.utils.shader_checker.optifine;

import com.github.andrew0030.pandora_core.utils.unsafe.FieldAccessor;
import com.github.andrew0030.pandora_core.utils.unsafe.TheUnsafeHelper;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.util.WorldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OptifineAccessor {
	public static boolean FALSE_BIND = false;
	public static final boolean optifinePresent;
	
	private static FieldAccessor program;
	private static FieldAccessor currentWorld;
	
	private static FieldAccessor BLOCK_VANILLA;
	private static FieldAccessor BLOCK_SHADERS;
	private static FieldAccessor ENTITY_VANILLA;
	private static FieldAccessor ENTITY_SHADERS;
	
	private static FieldAccessor CUSTOM_UNIFORMS;
	private static FieldAccessor SHADER_UNIFORMS;
	
	private static Method checkGlError;
	private static Method bindGbuffersTextures;
	
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
			SHADER_UNIFORMS = new FieldAccessor(theUnsafe, Shaders.class, "shaderUniforms");
			
			try {
				// TODO: method handle
				checkGlError = Shaders.class.getDeclaredMethod("checkGLError", String.class);
				checkGlError.setAccessible(true);
				
				bindGbuffersTextures = Shaders.class.getDeclaredMethod("bindGbuffersTextures");
				bindGbuffersTextures.setAccessible(true);
			} catch (Throwable err) {
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
}

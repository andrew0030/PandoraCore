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
import net.optifine.util.WorldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class OptifineAccessor {
	private static final boolean optifinePresent;
	
	private static FieldAccessor program;
	private static FieldAccessor currentWorld;
	
	private static FieldAccessor BLOCK_VANILLA;
	private static FieldAccessor BLOCK_SHADERS;
	private static FieldAccessor ENTITY_VANILLA;
	private static FieldAccessor ENTITY_SHADERS;
	
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
		Shaders.activeProgram = from;
		Shaders.activeProgramID = from.getId();
	}
	
	public static void falseUnbind() {
		Shaders.activeProgram = Shaders.ProgramNone;
		Shaders.activeProgramID = Shaders.ProgramNone.getId();
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
}

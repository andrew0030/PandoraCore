package com.github.andrew0030.pandora_core.utils.shader_checker.optifine;

import com.github.andrew0030.pandora_core.utils.TheUnsafeHelper;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import com.mojang.blaze3d.platform.GlStateManager;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class OptifineAccessor {
	private static final boolean optifinePresent;
	private static final Field programField;
	private static final Object programBase;
	private static final long programOffset;
	
	private static final Unsafe theUnsafe;
	
	static {
		theUnsafe = TheUnsafeHelper.getTheUnsafe();
		
		boolean presence = false;
		
		Field tempF = null;
		Object tempBase = 0;
		long tempOffset = 0;
		if (ShaderChecker.OF_HANDLER.isLoaded()) {
			presence = true;
			
			try {
				tempF = GlStateManager.class.getDeclaredField("glProgram");
			} catch (Throwable err) {
			}
			
			tempBase = theUnsafe.staticFieldBase(tempF);
			tempOffset = theUnsafe.staticFieldOffset(tempF);
		}
		
		optifinePresent = presence;
		programField = tempF;
		programBase = tempBase;
		programOffset = tempOffset;
	}
	
	public static void lieToOFAboutProgram(int id) {
		if (optifinePresent) {
			theUnsafe.putInt(programBase, programOffset, id);
		}
	}
}

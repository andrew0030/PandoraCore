package com.github.andrew0030.pandora_core.utils.debug;

import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import org.lwjgl.opengl.GL11;

public class RenderDebugger {
	public static void checkError(String label) {
		if (OptifineAccessor.optifinePresent) {
			OptifineAccessor.checkGlError(label);
		} else {
			int err = GL11.glGetError();
			if (err != 0) {
				System.out.println(label + " " + err);
			}
		}
	}
}

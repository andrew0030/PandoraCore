package com.github.andrew0030.pandora_core.mixin.test;

import net.minecraft.util.FrameTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;

@Mixin(FrameTimer.class)
public class RenderDoc {
	private static boolean initialized = false;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	public void preLoad(CallbackInfo ci) {
		if (initialized) return;
		
		try {
			String useRd = System.getProperty("paco.use.renderdoc");
			if (useRd != null && useRd.equals("true")) {
				System.out.println("Looking for renderdoc...");
				String pth = System.getenv("PATH");
				
				for (String s : pth.split(";")) {
					System.out.println(s);
					Path path = Path.of(s);
					Path chld = path.resolve("renderdoc.dll");
					if (chld.toFile().exists()) {
						System.load(s + File.separator + "renderdoc.dll");
						System.out.println("Renderdoc located!");
						break;
					}
				}
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		
		initialized = true;
	}
}

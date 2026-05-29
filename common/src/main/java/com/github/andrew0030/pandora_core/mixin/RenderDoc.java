package com.github.andrew0030.pandora_core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.util.FrameTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrameTimer.class)
public class RenderDoc {
	private static boolean initialized = false;
	
	@Inject(at = @At("RETURN"), method = "<init>")
	public void preLoad(CallbackInfo ci) {
		if (initialized) return;
		
//		try {
//			System.loadLibrary("renderdoc");
//		} catch (Throwable ignored) {
//		}
//		try {
//			System.loadLibrary("renderdoc.dll");
//		} catch (Throwable ignored) {
//		}
//		try {
//			System.load("renderdoc");
//		} catch (Throwable ignored) {
//		}
//		try {
//			System.load("renderdoc.dll");
//		} catch (Throwable ignored) {
//		}
		System.load("C:\\Program Files\\RenderDoc\\renderdoc.dll");
		initialized = true;
	}
}

package com.github.andrew0030.pandora_core.mixin.test;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * On windows, zink drivers have a bug which causes transparency to be displayed unconditionally
 * Minecraft's internal framebuffer, for some reason, has a transparency buffer that is not fully opaque by default
 * By clearing the color to fully opaque, this works around the bug
 *
 * Why am I patching the game specifically for zink drivers on windows, which mesa doesn't even really support?
 * Renderdoc has more support for Vulkan, and zink is OpenGL on Vulkan.
 *
 * To use mesa drivers on windows, go find some precompiled windows port and copy the binaries into the "bin" folder of your JDK
 *
 * To make sure renderdoc loads for VK properly, add environment variables
 * GALLIUM_DRIVER: zink
 * MESA_LOADER_DRIVER_OVERRIDE: zink
 * VK_ADD_LAYER_PATH: %RENDERDOC_PATH%
 * VK_INSTANCE_LAYERS: VK_LAYER_RENDERDOC_Capture
 *
 * Interestingly, D3D12 backend is crippled by sodium
 * I'm guessing said D3D12 backend is poorly implementing multidraw
 */
@Mixin(Minecraft.class)
public class ZinkPatch {
	@WrapOperation(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen(II)V"))
	public void preUpdate(RenderTarget instance, int width, int height, Operation<Void> original) {
		GL20.glClearColor(1, 1, 1, 1);
		GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		original.call(instance, width, height);
	}
}

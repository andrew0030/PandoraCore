package com.github.andrew0030.pandora_core.modules.templater.mixin.iris;

import com.github.andrew0030.pandora_core.modules.templater.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.loader.impl.iris.IrisTemplateLoader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.List;

@Mixin(value = Program.class, priority = 999)
public abstract class ProgramMixin {
	
	@Inject(at = @At("HEAD"), method = "compileShaderInternal")
	private static void preCompile(
			Program.Type type,
			String name,
			InputStream stream,
			String sourceName,
			GlslPreprocessor processor,
			CallbackInfoReturnable<Integer> cir
	) {
		if (sourceName.equals("<iris shaderpack shaders>")) {
			VanillaTemplateLoader.block();
			IrisTemplateLoader.unblock();
		}
	}
	
	@Inject(at = @At("RETURN"), method = "compileShaderInternal")
	private static void postCompile(
			Program.Type type,
			String name,
			InputStream stream,
			String sourceName,
			GlslPreprocessor processor,
			CallbackInfoReturnable<Integer> cir
	) {
		if (sourceName.equals("<iris shaderpack shaders>")) {
			VanillaTemplateLoader.cancel();
			IrisTemplateLoader.link();
			IrisTemplateLoader.block();
		}
	}
	
	@WrapOperation(method = "compileShaderInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;glShaderSource(ILjava/util/List;)V"))
	private static void wrapCompilation(int i, List<String> list, Operation<Void> original) {
		list = IrisTemplateLoader.shaderSource(list);
		
		original.call(i, list);
	}
}
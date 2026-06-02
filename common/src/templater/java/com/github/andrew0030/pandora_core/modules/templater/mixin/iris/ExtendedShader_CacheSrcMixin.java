package com.github.andrew0030.pandora_core.modules.templater.mixin.iris;

import com.github.andrew0030.pandora_core.modules.templater.loader.impl.iris.IrisTemplateLoader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.InputStream;

@Mixin(value = ExtendedShader.class)
public class ExtendedShader_CacheSrcMixin {
	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/shaders/Program;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)Lcom/mojang/blaze3d/shaders/Program;"
			),
			method = "lambda$iris$createExtraShaders$0"
	)
	protected Program wrapCreateShaderG(Program.Type pType, String pName, InputStream pShaderData, String pSourceName, GlslPreprocessor pPreprocessor, Operation<Program> original) {
		try {
			ResourceLocation loc = new ResourceLocation(pName);
			IrisTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + ".gsh");
			
			return original.call(
					pType, pName,
					pShaderData, pSourceName,
					pPreprocessor
			);
		} catch (Throwable err) {
			Iris.logger.error("Failed to create shader program", err);
			throw new RuntimeException("failed to load program");
		}
	}
	
//	@WrapOperation(
//			at = @At(
//					value = "INVOKE",
//					target = "Lcom/mojang/blaze3d/shaders/Program;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)Lcom/mojang/blaze3d/shaders/Program;"
//			),
//			method = "lambda$iris$createExtraShaders$1"
//	)
//	protected Program wrapCreateShaderTC(Program.Type pType, String pName, InputStream pShaderData, String pSourceName, GlslPreprocessor pPreprocessor, Operation<Program> original) {
//		try {
//			ResourceLocation loc = new ResourceLocation(pName);
//			IrisTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + ".tcs");
//
//			return original.call(
//					pType, pName,
//					pShaderData, pSourceName,
//					pPreprocessor
//			);
//		} catch (Throwable err) {
//			Iris.logger.error("Failed to create shader program", err);
//			throw new RuntimeException("failed to load program");
//		}
//	}
	
//	@WrapOperation(
//			at = @At(
//					value = "INVOKE",
//					target = "Lcom/mojang/blaze3d/shaders/Program;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)Lcom/mojang/blaze3d/shaders/Program;"
//			),
//			method = "lambda$iris$createExtraShaders$2"
//	)
//	protected Program wrapCreateShaderTE(Program.Type pType, String pName, InputStream pShaderData, String pSourceName, GlslPreprocessor pPreprocessor, Operation<Program> original) {
//		try {
//			ResourceLocation loc = new ResourceLocation(pName);
//			IrisTemplateLoader.activeFile(loc.getNamespace(), loc.getPath() + ".tes");
//
//			return original.call(
//					pType, pName,
//					pShaderData, pSourceName,
//					pPreprocessor
//			);
//		} catch (Throwable err) {
//			Iris.logger.error("Failed to create shader program", err);
//			throw new RuntimeException("failed to load program");
//		}
//	}
}

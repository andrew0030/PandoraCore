package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.itf.IPaCoExtOFProgram;
import com.github.andrew0030.pandora_core.modules.templater.loader.impl.optifine.OptifineTemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFTemplatedProgram;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OFVtxAttribute;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.optifine.shaders.Program;
import net.optifine.shaders.ProgramStage;
import net.optifine.shaders.Shaders;
import net.optifine.util.LineBuffer;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Shaders.class, remap = false)
public abstract class ShadersMixin {
	@Shadow
	public static int activeProgramID;
	
	@Shadow
	public static int checkGLError(String location) {
		throw new RuntimeException("");
	}
	
	@Inject(at = @At("HEAD"), method = "createGeomShader")
	private static void preCreateG(Program program, String filename, CallbackInfoReturnable<Integer> cir) {
		OptifineTemplateLoader.activeFile(filename);
		OptifineTemplateLoader.bindShader(filename, program);
	}
	
	@Inject(at = @At("HEAD"), method = "createVertShader")
	private static void preCreateV(Program program, String filename, CallbackInfoReturnable<Integer> cir) {
		OptifineTemplateLoader.activeFile(filename);
		OptifineTemplateLoader.bindShader(filename, program);
	}
	
	@Inject(at = @At("HEAD"), method = "createFragShader")
	private static void preCreateF(Program program, String filename, CallbackInfoReturnable<Integer> cir) {
		OptifineTemplateLoader.activeFile(filename);
		OptifineTemplateLoader.bindShader(filename, program);
	}
	
	@WrapOperation(
			method = "createVertShader(Lnet/optifine/shaders/Program;Ljava/lang/String;)I",
			at = @At(value = "INVOKE", target = "Lnet/optifine/util/LineBuffer;toString()Ljava/lang/String;")
	)
	private static String wrapLinesV(LineBuffer instance, Operation<String> original) {
		return pandoraCore$linkSource(original.call(instance));
	}
	
	@WrapOperation(
			method = "createFragShader(Lnet/optifine/shaders/Program;Ljava/lang/String;)I",
			at = @At(value = "INVOKE", target = "Lnet/optifine/util/LineBuffer;toString()Ljava/lang/String;")
	)
	private static String wrapLinesF(LineBuffer instance, Operation<String> original) {
		return pandoraCore$linkSource(original.call(instance));
	}
	
	@WrapOperation(
			method = "createGeomShader(Lnet/optifine/shaders/Program;Ljava/lang/String;)I",
			at = @At(value = "INVOKE", target = "Lnet/optifine/util/LineBuffer;toString()Ljava/lang/String;")
	)
	private static String wrapLinesG(LineBuffer instance, Operation<String> original) {
		return pandoraCore$linkSource(original.call(instance));
	}
	
	@Unique
	private static String pandoraCore$linkSource(String asString) {
		// TODO: paco mixin hook for source patching
		asString = OptifineTemplateLoader.shaderSource(List.of(asString));
		OptifineTemplateLoader.link();
		return asString;
	}
	
	@Inject(at = @At("TAIL"), method = "init")
	private static void postInit(CallbackInfo ci) {
		OptifineTemplateLoader.getInstance().performReload();
	}
	
	@Inject(at = @At("TAIL"), method = "uninit")
	private static void postUninit(CallbackInfo ci) {
		OptifineTemplateLoader.getInstance().dumpShaders();
	}
	
	@Inject(at = @At("RETURN"), method = "setupProgram")
	private static void finishProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath, CallbackInfo ci) {
		OptifineTemplateLoader.bindShader(program.getName(), program);
		
		IPaCoExtOFProgram ext = (IPaCoExtOFProgram) program;
		for (OFVtxAttribute value : OFVtxAttribute.values()) {
			if (value.progUseAttrib.get()) {
				ext.pandoraCore$enableAttrib(value);
			}
		}
	}
	
//	@WrapOperation(method = "useProgram", at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glUseProgram(I)V"))
//	private static void swapProgram(int i, Operation<Void> original) {
//		if (OFTemplatedProgram.useProgram == null) {
//			original.call(i);
//		}
//	}
	
	@WrapOperation(method = "useProgram", at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glUseProgram(I)V"))
	private static void swapProgram(int i, Operation<Void> original) {
		if (OptifineAccessor.FALSE_BIND) {
			// do nothing!
			return;
		}
		
		if (OFTemplatedProgram.useProgram == null) {
			original.call(i);
//			original.call(i);
		} else {
//			activeProgramID = OFTemplatedProgram.useProgram.getId();
//			activeProgramID = OFTemplatedProgram.useProgram.getId();
			checkGLError("pre-paco-bindInstanced");
//			GlStateManager._glUseProgram(i);
			OptifineAccessor.lieToOFAboutProgram(i);
			GL20.glUseProgram(OFTemplatedProgram.useProgram.getId());
			checkGLError("paco-bindInstanced");
		}
	}
	
	@WrapOperation(method = "useProgram", at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/Shaders;setProgramUniforms(Lnet/optifine/shaders/ProgramStage;)V"))
	private static void wrapSetUforms(ProgramStage stage, Operation<Void> original) {
		if (OptifineAccessor.FALSE_BIND) {
			return;
		}

		original.call(stage);
	}
}

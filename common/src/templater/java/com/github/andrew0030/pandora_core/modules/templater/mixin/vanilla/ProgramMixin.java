package com.github.andrew0030.pandora_core.modules.templater.mixin.vanilla;

import com.github.andrew0030.pandora_core.modules.templater.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoAccessibleProgram;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.List;

@Mixin(Program.class)
public abstract class ProgramMixin implements IPaCoAccessibleProgram {
    @Shadow private int id;

    @Inject(at = @At("RETURN"), method = "compileShaderInternal")
    private static void postCompile(
            Program.Type type,
            String name,
            InputStream stream,
            String sourceName,
            GlslPreprocessor processor,
            CallbackInfoReturnable<Integer> cir
    ) {
        VanillaTemplateLoader.link();
    }

    @Override
    public int pandoraCore$getId() {
        return id;
    }
	
	@WrapOperation(method = "compileShaderInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;glShaderSource(ILjava/util/List;)V"))
	private static void wrapCompilation(int shaderID, List<String> list, Operation<Void> original) {
		list = VanillaTemplateLoader.shaderSource(list);
		
		original.call(shaderID, list);
	}
}
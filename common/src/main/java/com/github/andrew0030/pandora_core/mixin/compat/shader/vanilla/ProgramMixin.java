package com.github.andrew0030.pandora_core.mixin.compat.shader.vanilla;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoAccessibleProgram;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

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
}
package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.modules.templater.itf.INamedShader;
import net.optifine.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Program.class)
public abstract class ProgramMixin implements INamedShader {
	@Shadow
	public abstract String getName();
	
	@Override
	public String pandoraCore$getName() {
		return getName();
	}
}

package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.core.IPaCoUniformListable;
import com.github.andrew0030.pandora_core.modules.templater.itf.PaCoOFUniformListable;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import net.optifine.shaders.uniform.ShaderUniformBase;
import net.optifine.shaders.uniform.ShaderUniforms;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderUniforms.class)
public class ShaderUniformsMixin implements PaCoOFUniformListable {
	@Shadow
	@Final
	private List<ShaderUniformBase> listUniforms;
	
	@Override
	public Iterable<ShaderUniformBase> getUforms() {
		return listUniforms;
	}
}

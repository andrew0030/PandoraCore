package com.github.andrew0030.pandora_core.modules.templater.itf;

import net.optifine.shaders.uniform.ShaderUniformBase;

public interface PaCoOFUniformListable {
	Iterable<ShaderUniformBase> getUforms();
}

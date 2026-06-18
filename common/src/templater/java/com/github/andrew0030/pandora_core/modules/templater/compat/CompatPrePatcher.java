package com.github.andrew0030.pandora_core.modules.templater.compat;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import tfc.glsl.GlslFile;

public abstract class CompatPrePatcher {
	public abstract void apply(VariableMapper mapper, GlslFile tree, TemplateTransformation transformation);
}

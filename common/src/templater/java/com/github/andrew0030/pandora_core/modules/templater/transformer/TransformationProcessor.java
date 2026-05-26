package com.github.andrew0030.pandora_core.modules.templater.transformer;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;

public abstract class TransformationProcessor {
    public abstract String process(VariableMapper mapper, String source, TemplateTransformation transformation);
}

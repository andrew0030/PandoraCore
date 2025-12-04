package com.github.andrew0030.pandora_core.client.shader.templating.transformer;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;

public abstract class TransformationProcessor {
    public abstract String process(VariableMapper mapper, String source, TemplateTransformation transformation);
}

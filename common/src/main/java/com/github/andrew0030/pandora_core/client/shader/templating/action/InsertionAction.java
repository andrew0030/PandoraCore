package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import io.github.ocelot.glslprocessor.api.node.GlslNode;

public abstract class InsertionAction {
    public String headInjection(TemplateTransformation transformation) {
        return null;
    }

    public String afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var) {
        return null;
    }
}

package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;

public abstract class InsertionAction {
    public String headInjection(TemplateTransformation transformation) {
        return null;
    }

    public String afterInputVar(TemplateTransformation transformation, String type, String var) {
        return null;
    }
}

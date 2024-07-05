package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;

public class OnDemandTemplateShader extends TemplatedShader {
    TemplateManager.LoadManager manager;

    public OnDemandTemplateShader(TemplateLoader loader, TemplateTransformation transformation, String template, TemplateManager.LoadManager manager) {
        super(loader, transformation, template);
        this.manager = manager;
    }

    @Override
    public void apply() {
        TemplatedShader shader = manager.reload(transformation);
        if (shader != null) shader.apply();
    }

    @Override
    public void upload() {
        TemplatedShader shader = manager.reload(transformation);
        if (shader != null) shader.upload();
    }

    @Override
    public void destroy() {
        // why would I load something only to destroy it?
        // that's counterproductive, lol
    }
}

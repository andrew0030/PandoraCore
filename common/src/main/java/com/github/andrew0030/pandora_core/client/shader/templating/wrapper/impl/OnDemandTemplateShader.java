package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.mojang.blaze3d.shaders.AbstractUniform;

public class OnDemandTemplateShader extends TemplatedShader {
    TemplateManager.LoadManager manager;
    TemplatedShader direct;

    public TemplatedShader getDirect() {
        if (direct == null) direct = manager.reload(transformation);
        return direct;
    }

    public OnDemandTemplateShader(TemplateLoader loader, TemplateTransformation transformation, String template, TemplateManager.LoadManager manager) {
        super(loader, transformation, template);
        this.manager = manager;
    }

    @Override
    public void apply() {
        getDirect().apply();
    }

    @Override
    public void upload() {
        getDirect().upload();
    }

    @Override
    public void destroy() {
        if (direct != null)
            direct.destroy();
    }

    @Override
    public boolean matches(String mod, String active) {
        if (direct !=  null)
            return direct.matches(mod, active);
        return false;
    }

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        return getDirect().getUniform(name, type, count);
    }
}

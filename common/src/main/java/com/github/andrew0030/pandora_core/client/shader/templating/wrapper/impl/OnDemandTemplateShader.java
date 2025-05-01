package com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.mojang.blaze3d.shaders.AbstractUniform;

public class OnDemandTemplateShader extends TemplatedShader {
    TemplateManager.LoadManager manager;
    TemplatedShader direct;

    public TemplatedShader getDirect() {
        if (direct == null) direct = manager.reload(transformation);
        return direct;
    }

    public OnDemandTemplateShader(TemplateLoader loader, TemplateShaderResourceLoader.TemplateStruct transformation, String template, TemplateManager.LoadManager manager) {
        super(loader, transformation, template);
        this.manager = manager;
    }

    @Override
    public void apply() {
        TemplatedShader sdr = getDirect();
        if (sdr != null) sdr.apply();
    }

    @Override
    public void upload() {
        TemplatedShader sdr = getDirect();
        if (sdr != null) sdr.upload();
    }

    @Override
    public void destroy() {
        if (direct != null)
            direct.destroy();
    }

    @Override
    public AbstractUniform getUniform(String name, int type, int count) {
        TemplatedShader sdr = getDirect();
        if (sdr != null) return sdr.getUniform(name, type, count);
        return ABSTRACT_INST;
    }

    @Override
    public boolean hasDirect() {
        return false;
    }
}

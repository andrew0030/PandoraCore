package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;

public abstract class TemplateLoader {
    /**
     * Attempts to load the shader from the defined template transformation
     * If this returns false, {@link TemplateManager#loadTemplate()} continues onto the next loader
     *
     * @param transformation the template transformation to load the shader using
     * @return whether the loader was able to successfully load the shader
     *         ideally, this never returns false, but that's probably not going to happen
     */
    public abstract boolean attempt(TemplateManager.LoadManager manager, TemplateTransformation transformation);

    public abstract TransformationProcessor processor();

    public abstract String name();
}

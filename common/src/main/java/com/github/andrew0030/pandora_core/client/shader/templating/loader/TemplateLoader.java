package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;

public abstract class TemplateLoader {
    /**
     * Attempts to load the shader from the defined template transformation
     *
     * @param transformation the template transformation to load the shader using
     * @return a result indicating how the load went
     * {@link LoadResult#UNCACHED} indicates that the shader couldn't be loaded because the source hasn't been cached yet
     * in this event, an OnDemandTemplateShader will be loaded
     * {@link LoadResult#FAILED}   indicates that the shader failed to load or the shader loader doesn't support the template types specified by the transformation
     * in this event, the shader manager goes onto the next loader
     * {@link LoadResult#LOADED}   indicates that the shader load successfully
     */
    public abstract LoadResult attempt(TemplateManager.LoadManager manager, TemplateTransformation transformation);

    /**
     * Attempts to load the shader from the defined template transformation
     * This should run more or less the same code as {@link TemplateLoader#attempt(TemplateManager.LoadManager, TemplateTransformation)}
     * However, unlike attempt, this should also make an effort to force the shader's source into the cache if it's not already present
     * If this returns false, {@link TemplateManager#loadTemplate(TemplateTransformation, boolean)}} continues onto the next loader
     *
     * @param transformation the template transformation to load the shader using
     * @return whether the loader was able to successfully load the shader
     * ideally, this never returns false, but that's probably not going to happen
     */
    public abstract boolean attemptComplete(TemplateManager.LoadManager manager, TemplateTransformation transformation);

    public abstract TransformationProcessor processor();

    public abstract String name();

    public abstract void beginReload();

    public abstract boolean matches(TemplatedShader direct, String shader);

    public enum LoadResult {
        UNCACHED,
        LOADED,
        FAILED,
    }
}

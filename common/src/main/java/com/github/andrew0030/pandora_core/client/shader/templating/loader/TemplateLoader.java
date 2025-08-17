package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class TemplateLoader {
    public final ShaderCapabilities capabilities;

    public TemplateLoader(ShaderCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Attempts to load the shader from the defined template transformation
     *
     * @param struct          a representation of the transformation's json
     * @param transformations a map containing the files for the template transformation
     * @return a result indicating how the load went
     * {@link LoadResult#UNCACHED} indicates that the shader couldn't be loaded because the source hasn't been cached yet
     * in this event, an OnDemandTemplateShader will be loaded
     * {@link LoadResult#FAILED}   indicates that the shader failed to load or the shader loader doesn't support the template types specified by the transformation
     * in this event, the shader manager goes onto the next loader
     * {@link LoadResult#LOADED}   indicates that the shader load successfully
     */
    public abstract LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, Function<String, TemplateTransformation> transformations);

    public abstract TransformationProcessor processor();

    public abstract String name();

    protected abstract void _beginReload();

    public abstract boolean manuallyReloaded();

    public final void beginReload() {
        if (!manuallyReloaded()) {
            for (TemplatedShader value : loadedShaders.values()) {
                if (value.hasDirect())
                    value.destroy();
            }
            loadedShaders.clear();
            _beginReload();
        }
    }

    public boolean supports(ShaderCapability[] requestedCapabilities) {
        return capabilities.supports(requestedCapabilities);
    }

    /**
     * Run preparation steps for resource loading
     *
     * @param manager the resource manager
     */
    public abstract void prepare(ResourceManager manager);

    public void preload(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, Function<String, TemplateTransformation> transformations) {
        attempt(manager, struct, transformations);
    }

    public void debug(List<String> list) {
        list.add(loadedShaders.size() + " loaded " + name() + " templates");
        list.add("Capabilities: " + capabilities.debugString());
    }

    private Map<ResourceLocation, TemplatedShader> loadedShaders = new HashMap<>();

    protected void loadShader(ResourceLocation location, TemplatedShader instance) {
        loadedShaders.put(location, instance);
    }

    public TemplatedShader getShader(ResourceLocation location) {
        return loadedShaders.get(location);
    }

    protected void load(TemplateManager.LoadManager manager, TemplatedShader templateShader) {
        loadShader(templateShader.location(), templateShader);
        manager.loaded(templateShader.location());
    }

    public void performReload() {
        TemplateManager.reloadLoader(this);
    }

    public void dumpShaders() {
        loadedShaders.forEach((k, v) -> {
            TemplateManager.invalidateShader(k);
            if (v.hasDirect())
                v.destroy();
        });
        loadedShaders.clear();
    }

    public enum LoadResult {
        UNCACHED,
        LOADED,
        FAILED,
    }
}

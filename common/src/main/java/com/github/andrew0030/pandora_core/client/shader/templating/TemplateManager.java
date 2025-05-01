package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateManager {
    public static final Gson GSON = new GsonBuilder().setLenient().create();

    /**
     * Gets a {@link TemplatedShaderInstance}
     * This instance may arbitrarily switch between shader loaders, including without reloading resources/shaders
     *
     * @param resource a {@link ResourceLocation} pointing to the template's glsl file
     * @return the corresponding template shader instance
     */
    public static ShaderWrapper getWrapper(ResourceLocation resource) {
        ShaderWrapper wrapper = WRAPPERS.get(resource);

        if (wrapper == null) {
            wrapper = new ShaderWrapper(resource);
            WRAPPERS.put(resource, wrapper);
        }
        return wrapper;
    }

    private static final ArrayList<TemplateLoader> LOADERS = new ArrayList<>();
    private static final HashMap<ResourceLocation, ShaderWrapper> WRAPPERS = new HashMap<>();

    /**
     * Construct template loaders
     * If a mod wants to add its own, it should mixin to this
     * <p>
     * Why is this a method?
     * Because javadocs don't work on static init.
     */
    private static void init() {
        if (
                Services.PLATFORM.isModLoaded("iris") ||
                        Services.PLATFORM.isModLoaded("oculus")
        )
            LOADERS.add(new IrisTemplateLoader());
        LOADERS.add(new VanillaTemplateLoader());
    }

    static {
        init();
    }

    public static void writeF3(List<String> list) {
        list.add("Wrappers: " + WRAPPERS.size());
        for (TemplateLoader loader : LOADERS) {
            loader.debug(list);
        }
    }

    public static void reloadTemplate(TemplateLoader instance, String s) {
    }

    public void beginReload() {
        for (TemplateLoader loader : LOADERS) {
            loader.beginReload();
        }
    }

    public List<TemplateLoader> allLoaders() {
        return ImmutableList.copyOf(LOADERS);
    }

    public void preload(Map<String, TemplateTransformation> transformations, List<TemplateShaderResourceLoader.TemplateStruct> result) {
        LoadManager manager = new LoadManager();
        for (TemplateShaderResourceLoader.TemplateStruct struct : result) {
            for (TemplateLoader loader : LOADERS) {
                // shaders may fail to preload
                // that is fine, and completely expected
                loader.preload(
                        manager,
                        struct,
                        transformations::get
                );
            }
        }
    }

    public class LoadManager {
        public void load(TemplatedShader vanillaTemplatedShader) {
        }

        public TemplatedShader reload(TemplateShaderResourceLoader.TemplateStruct transformation) {
            throw new RuntimeException("TODO");
        }
    }
}

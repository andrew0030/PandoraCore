package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.platform.Services;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        return WRAPPERS.get(new ResourceLocation(
                resource.getNamespace(),
                resource.getPath()
        ));
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
        // TODO: update
//        int loaded = 0;
//        HashMap<TemplateLoader, Integer> counts = new HashMap<>();
//        for (TemplatedShaderInstance value : TEMPLATED.values()) {
//            TemplatedShader direct = value.getDirect();
//            if (!(direct instanceof OnDemandTemplateShader)) {
//                loaded++;
//
//                int v = counts.getOrDefault(direct.getLoader(), 0);
//                counts.put(direct.getLoader(), v + 1);
//            }
//        }
//
//        list.add("Loaded templates: " + (loaded + " / " + WRAPPERS.size()));
//        list.add("Template Loaders:");
//        int index = 0;
//        for (TemplateLoader loader : LOADERS) {
//            list.add(index++ + " -> " + loader.name() + ": " + counts.getOrDefault(loader, 0));
//        }
//        list.add("Unloaded: " + (TEMPLATED.size() - loaded));
    }

    public void beginReload() {
        // TODO:
    }

    public List<TemplateLoader> allLoaders() {
        return ImmutableList.copyOf(LOADERS);
    }

    public void preload(List<TemplateShaderResourceLoader.TemplateStruct> result) {
        // TODO
    }
}

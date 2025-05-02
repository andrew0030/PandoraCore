package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapability;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.blackhole.VoidShader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateManager {
    public static final Gson GSON = new GsonBuilder().setLenient().create();

    private static TemplateManager INSTANCE;

    @ApiStatus.Internal
    private static Throwable cause;

    public TemplateManager() {
        if (cause != null) {
            RuntimeException e = new RuntimeException("Cannot create multiple template managers", cause);
            throw e;
        }
        cause = new Throwable("Previously created by");
        cause.setStackTrace(Thread.currentThread().getStackTrace());
        INSTANCE = this;
    }

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

//    public static void reloadTemplate(TemplateLoader instance, String s) {
//    }

    public static TemplatedShader choose(ShaderCapability[] requestedCapabilities, Map<ShaderLoader, TemplatedShader> instances, ResourceLocation location) {
        for (TemplateLoader loader : LOADERS) {
            if (loader.supports(requestedCapabilities)) {
                TemplatedShader instance = instances.get(loader);
                if (instance == null) {
//                    throw new RuntimeException("TODO");
                    instance = loader.getShader(location);
                    if (instance == null) {
                        continue;
                    }
                }
                return instance;
            }
        }

        TemplatedShader fallback = VanillaTemplateLoader.getInstance().getShader(location);
        if (fallback == null)
            return VoidShader.INSTANCE;
        return fallback;
    }

    public static void invalidateShader(ResourceLocation k) {
        ShaderWrapper wrapper = WRAPPERS.get(k);
        if (wrapper != null) wrapper.clearCache();
    }

    public void beginReload() {
        for (TemplateLoader loader : LOADERS) {
            loader.beginReload();
        }
    }

    public List<TemplateLoader> allLoaders() {
        return ImmutableList.copyOf(LOADERS);
    }

    Map<String, TemplateTransformation> transformations;
    List<TemplateShaderResourceLoader.TemplateStruct> structs;

    public static void reloadLoader(TemplateLoader templateLoader) {
        LoadManager manager = INSTANCE.new LoadManager();
        for (TemplateShaderResourceLoader.TemplateStruct struct : INSTANCE.structs) {
            templateLoader.attempt(
                    manager,
                    struct,
                    INSTANCE.transformations::get
            );
        }
    }

    public void preload(Map<String, TemplateTransformation> transformations, List<TemplateShaderResourceLoader.TemplateStruct> result) {
        this.transformations = transformations;
        this.structs = result;

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

    // the point of this is to be accessible, but not instantiable to external code
    @SuppressWarnings("InnerClassMayBeStatic")
    public class LoadManager {
        public TemplatedShader reload(TemplateShaderResourceLoader.TemplateStruct transformation) {
            throw new RuntimeException("TODO");
        }

        public void loaded(ResourceLocation location) {
            ShaderWrapper wrapper = WRAPPERS.get(location);
            if (wrapper != null) wrapper.clearCache();
        }
    }
}

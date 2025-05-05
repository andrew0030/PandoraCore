package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapability;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.ShaderWrapper;
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
     * Gets a {@link ShaderWrapper}
     * This shader wrapper may represent multiple shaders at a time
     * For instance, with iris, vanilla core shaders are used for UI while iris shaders are used for the world
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

    /**
     * Selects a templated shader based on teh requested capabilities
     * If none are applicable, use the vanilla shader
     *
     * @param requestedCapabilities the required shader capabilities
     * @param instances             the internal cache of the shader, loader->templated shader
     * @param location              the location of teh template shader
     * @return the templated shader to use for the active render context
     */
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

    /**
     * Invalidates a shader's internal cache
     * This should be called whenever shaders load/unload on the fly
     * For an example, see the iris shader loader
     *
     * @param location the resource location of the templated shader
     */
    public static void invalidateShader(ResourceLocation location) {
        ShaderWrapper wrapper = WRAPPERS.get(location);
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
        if (INSTANCE.structs == null)
            return;

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
        public void loaded(ResourceLocation location) {
            ShaderWrapper wrapper = WRAPPERS.get(location);
            if (wrapper != null) wrapper.clearCache();
        }
    }
}

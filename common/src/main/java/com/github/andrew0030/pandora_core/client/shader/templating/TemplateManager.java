package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.IrisTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.OnDemandTemplateShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.VanillaTemplatedShader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TemplateManager {
    /**
     * Gets a {@link TemplatedShaderInstance}
     * This instance may arbitrarily switch between shader loaders, including without reloading resources/shaders
     *
     * @param resource a {@link ResourceLocation} pointing to the template's glsl file
     * @return the corresponding template shader instance
     */
    public static TemplatedShaderInstance getTemplated(ResourceLocation resource) {
        return TEMPLATED.get(new ResourceLocation(
                resource.getNamespace(),
                resource.getPath()
        ));
    }

    // TODO: this is stupidly programmed and should be redone later
    //       for now it works
    @ApiStatus.Experimental
    public static void reloadTemplate(
            TemplateLoader loader,
            String shader
    ) {
        int index = LOADERS.indexOf(loader);
        for (TemplatedShaderInstance value : TEMPLATED.values()) {
            TemplatedShader direct = value.getDirect();
            boolean match = false;
            for (TemplateLoader templateLoader : LOADERS) {
                if (templateLoader.matches(
                        value.getDirect(), shader,
                        direct.transformation().getTemplates(), transformations
                )) {
                    match = true;
                    break;
                }
            }
            if (match) {
                TemplateLoader loadedBy = direct.getLoader();
                int idex = LOADERS.indexOf(loadedBy);
                if (index <= idex) {
                    if (loadTemplate(direct.transformation(), true)) {
                        direct.destroy();
                    }
                }
            }
        }
    }

    @ApiStatus.Internal
    private static Throwable cause;
    private static LoadManager templateLoadManager;

    @ApiStatus.Internal
    public TemplateManager() {
        if (cause != null) {
            RuntimeException e = new RuntimeException("Cannot create multiple template managers", cause);
            throw e;
        }
        cause = new Throwable("Previously created by");
        cause.setStackTrace(Thread.currentThread().getStackTrace());
        templateLoadManager = new LoadManager();
    }

    public static List<TemplateLoader> LOADERS = new ArrayList<>();

    @ApiStatus.Internal
    private static final Map<String, TemplateTransformation> TRANSFORMATION_MAP = new HashMap<>();

    @ApiStatus.Internal
    /**
     * lambda statement for getting shader transformation objects from the map
     * provided as a lambda to help ensure immutability
     */
    private static final Function<String, TemplateTransformation> transformations = (str) -> {
        TemplateTransformation trf = TRANSFORMATION_MAP.get(str);
        if (trf == null)
            throw new RuntimeException("Shader transformation " + str + " not found.");
        return trf;
    };

    @ApiStatus.Internal
    private static final Map<ResourceLocation, JsonObject> JSONS = new HashMap<>();
    @ApiStatus.Internal
    private static final HashMap<ResourceLocation, TemplatedShaderInstance> TEMPLATED = new HashMap<>();

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders");

    protected static final Gson GSON = new GsonBuilder().setLenient().create();

    @ApiStatus.Internal
    public void beginReload() {
        TRANSFORMATION_MAP.clear();
        for (TemplateLoader loader : LOADERS) {
            loader.beginReload();
        }
    }

    @ApiStatus.Internal
    public void register(TemplateTransformation transformation) {
        TRANSFORMATION_MAP.put(transformation.location.toString(), transformation);
    }

    @ApiStatus.Internal
    public void addJson(ResourceLocation location, String string) {
        JSONS.put(location, GSON.fromJson(string, JsonObject.class));
    }

    @ApiStatus.Internal
    protected static boolean loadTemplate(TemplateShaderResourceLoader.TemplateStruct struct, boolean complete) {
        Map<String, String> transformers = struct.getTransformers();
        for (TemplateLoader loader : LOADERS) {
            TemplateLoader.LoadResult result = complete ?
                    (loader.attemptComplete(templateLoadManager, struct, transformers, transformations) ? TemplateLoader.LoadResult.LOADED : TemplateLoader.LoadResult.FAILED) :
                    loader.attempt(templateLoadManager, struct, transformers, transformations);
            if (result == TemplateLoader.LoadResult.LOADED)
                return true;
            if (result == TemplateLoader.LoadResult.UNCACHED) {
                templateLoadManager.load(new OnDemandTemplateShader(
                        loader, struct,
                        null, templateLoadManager
                ));
                return true;
            }
        }

        LOGGER.warn("Failed to load template shader " + struct.location.toString());
        return false;
    }

    /**
     * Reloads all template shaders
     * This gets called when the template resource loader finishes
     * <p>
     * Template shaders may also be reloaded on the fly as necessary
     */
    @ApiStatus.Internal
    public void reload(List<TemplateShaderResourceLoader.TemplateStruct> structs) {
        boolean failedAny = false;

        // reload shaders
        for (TemplateShaderResourceLoader.TemplateStruct struct : structs) {
            try {
                boolean result = loadTemplate(struct, false);
                if (result) continue;
                failedAny = true;
            } catch (Throwable err) {
                LOGGER.error("An expected error occured while loading template shader: " + struct.location, err);
            }
        }

        // dump info about active template shader loaders
        if (failedAny) {
            StringBuilder builder = new StringBuilder();
            builder.append("Current template shader loaders:\n");
            for (TemplateLoader loader : LOADERS)
                builder.append("- ").append(loader.name()).append("\n");
            LOGGER.info(builder.toString().trim());
        }
    }

    public final class LoadManager {
        /**
         * Loads a templated shader into the cache
         * Only to be called by template loaders
         *
         * @param shader the shader to load
         */
        public void load(TemplatedShader shader) {
            TemplatedShaderInstance instance = TEMPLATED.get(shader.location());
            if (instance == null) {
                TEMPLATED.put(shader.location(), new TemplatedShaderInstance(shader, shader.transformation()));
            } else {
                instance.getDirect().destroy();
                instance.updateDirect(shader);
            }
        }

        public TemplatedShader reload(TemplateShaderResourceLoader.TemplateStruct transformation) {
            if (loadTemplate(transformation, true)) {
                return getTemplated(transformation.location).getDirect();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Failed to load template shader " + transformation.location + "\n");
                builder.append("Current template shader loaders:\n");
                for (TemplateLoader loader : LOADERS)
                    builder.append("- ").append(loader.name()).append("\n");
                LOGGER.info(builder.toString().trim());
            }
            return null;
        }
    }

    /**
     * Construct template loaders
     * If a mod wants to add its own, it should mixin to this
     * <p>
     * Why is this a method?
     * Because javadocs don't work on static init.
     */
    public static void init() {
        if (
                Services.PLATFORM.isModLoaded("iris") ||
                        Services.PLATFORM.isModLoaded("oculus")
        )
            LOADERS.add(new IrisTemplateLoader());
        LOADERS.add(new VanillaTemplateLoader(JSONS));
    }

    static {
        init();
    }

    public static void writeF3(List<String> list) {
        int loaded = 0;
        HashMap<TemplateLoader, Integer> counts = new HashMap<>();
        for (TemplatedShaderInstance value : TEMPLATED.values()) {
            TemplatedShader direct = value.getDirect();
            if (!(direct instanceof OnDemandTemplateShader)) {
                loaded++;

                int v = counts.getOrDefault(direct.getLoader(), 0);
                counts.put(direct.getLoader(), v + 1);
            }
        }

        list.add("Loaded templates: " + (loaded + " / " + TEMPLATED.size()));
        list.add("Template Loaders:");
        int index = 0;
        for (TemplateLoader loader : LOADERS) {
            list.add(index++ + " -> " + loader.name() + ": " + counts.getOrDefault(loader, 0));
        }
        list.add("Unloaded: " + (TEMPLATED.size() - loaded));
    }
}

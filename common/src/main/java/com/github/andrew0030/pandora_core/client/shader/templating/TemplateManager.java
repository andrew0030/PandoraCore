package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShaderInstance;
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

public class TemplateManager {
    /**
     * Gets a template shader instance
     * This instance may arbitrarily switch between shader loaders, including without reloading resources/shaders
     *
     * @param resource a resource location pointing to the template's glsl file
     * @return the corresponding template shader instance
     */
    public static TemplatedShaderInstance getTemplated(ResourceLocation resource) {
        throw new RuntimeException("TODO");
    }

    public static void reloadTemplate(
            String modRef,
            String mod,
            String active
    ) {
    }

    @ApiStatus.Internal
    private static Throwable cause;

    @ApiStatus.Internal
    public TemplateManager() {
        if (cause != null) {
            RuntimeException e = new RuntimeException("Cannot create multiple template managers", cause);
            throw e;
        }
        cause = new Throwable("Previously created by");
        cause.setStackTrace(Thread.currentThread().getStackTrace());
    }

    public static List<TemplateLoader> LOADERS = new ArrayList<>();

    @ApiStatus.Internal
    private static final ArrayList<TemplateTransformation> TRANSFORMATIONS = new ArrayList<>();
    @ApiStatus.Internal
    private static final ArrayList<JsonObject> JSONS = new ArrayList<>();
    @ApiStatus.Internal
    private static final HashMap<String, TemplatedShader> TEMPLATED = new HashMap<>();

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders");

    private static final Gson GSON = new GsonBuilder().setLenient().create();

    @ApiStatus.Internal
    public void beginReload() {
        TRANSFORMATIONS.clear();
        JSONS.clear();
    }

    @ApiStatus.Internal
    public void register(TemplateTransformation transformation) {
        TRANSFORMATIONS.add(transformation);
    }

    @ApiStatus.Internal
    public void addJson(String string) {
        JSONS.add(GSON.fromJson(string, JsonObject.class));
    }

    protected static boolean loadTemplate(TemplateTransformation transformation) {
        for (TemplateLoader loader : LOADERS) {
            if (loader.attempt(transformation))
                return true;
        }
        LOGGER.warn("Failed to load template shader " + transformation.location.toString());
        return false;
    }

    /**
     * Reloads all template shaders
     * This gets called when the template resource loader finishes
     * Template shaders may also be reloaded on the fly as necessary
     */
    @ApiStatus.Internal
    public void reload() {
        boolean failedAny = false;

        for (TemplateTransformation transformation : TRANSFORMATIONS) {
            boolean result = loadTemplate(transformation);
            if (result) continue;
            failedAny = true;
        }

        if (failedAny) {
            StringBuilder builder = new StringBuilder();
            builder.append("Current template shader loaders:\n");
            for (TemplateLoader loader : LOADERS)
                builder.append("- ").append(loader.name()).append("\n");
            LOGGER.info(builder.toString().trim());
        }
    }

    /**
     * Construct template loaders
     * If a mod wants to add its own, it should mixin to "<clinit>" here
     */
    static {
        LOADERS.add(new VanillaTemplateLoader(JSONS));
    }
}

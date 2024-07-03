package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShader;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateManager {
    private static Throwable cause;

    public TemplateManager() {
        if (cause != null) {
            RuntimeException e = new RuntimeException("Cannot create multiple template managers", cause);
            throw e;
        }
        cause = new Throwable("Previously created by");
        cause.setStackTrace(Thread.currentThread().getStackTrace());
    }

    public static List<TemplateLoader> LOADERS = new ArrayList<>();

    static {
        LOADERS.add(new VanillaTemplateLoader());
    }

    private static final ArrayList<TemplateTransformation> TRANSFORMATIONS = new ArrayList<>();
    private static final ArrayList<JsonObject> JSONS = new ArrayList<>();

    private static final HashMap<String, TemplatedShader> TEMPLATED = new HashMap<>();
    private static final DualKeyMap<String, String, TemplatedShader> templatesIris = new DualKeyMap<>(new Object2ObjectRBTreeMap<>());
    private static final DualKeyMap<String, String, TemplatedShader> templatesVanilla = new DualKeyMap<>(new Object2ObjectRBTreeMap<>());

    public static TemplatedShader getTemplated(String resource) {
        throw new RuntimeException("TODO");
    }

    public static void reloadTemplate(
            String modRef,
            String mod,
            String active
    ) {
    }

    public void beginReload() {
        TRANSFORMATIONS.clear();
        JSONS.clear();
    }

    public void register(TemplateTransformation transformation) {
        TRANSFORMATIONS.add(transformation);
    }

    private static final Gson GSON = new GsonBuilder().setLenient().create();

    public void addJson(String string) {
        JSONS.add(GSON.fromJson(string, JsonObject.class));
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders");

    public void reload() {
        transformationLoop:
        for (TemplateTransformation transformation : TRANSFORMATIONS) {
            for (TemplateLoader loader : LOADERS) {
                boolean result = loader.attempt(transformation);
                if (result) continue transformationLoop;
            }
            try {
                // TODO
                LOGGER.warn("Failed to load template shader " + "TODO: proper feedback for file");
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }
    }
}

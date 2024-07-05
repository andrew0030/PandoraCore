package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.VanillaTemplatedShader;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderParser;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.Shader;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class VanillaTemplateLoader extends TemplateLoader {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

    private final Map<ResourceLocation, JsonObject> shaderJsons;
    private final TransformationProcessor processor = new DefaultTransformationProcessor();

    private static VanillaTemplateLoader INSTANCE;

    public VanillaTemplateLoader(Map<ResourceLocation, JsonObject> shaderJsons) {
        if (INSTANCE != null)
            throw new RuntimeException("Cannot create two vanilla template loaders.");
        this.shaderJsons = shaderJsons;
        INSTANCE = this;
    }

    public static void activeFile(String source, String file) {
        MOD = source;
        ACTIVE = file;
        SOURCE = new ArrayList<>();
    }

    public static void shaderSource(List<String> $$1) {
        SOURCE.addAll($$1);
    }

    static boolean forceLoad = false;

    public static void link() {
        sources.put(MOD, ACTIVE, new ReadOnlyList<>(SOURCE));
        if (!forceLoad)
            TemplateManager.reloadTemplate(INSTANCE, MOD, ACTIVE);
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Vanilla");

    private static final Map<String, ShaderInstance> instances = new HashMap<>();

    public static void bindShader(String $$1, ShaderInstance shaderInstance) {
        instances.put($$1, shaderInstance);
    }

    public static void unbindShader(String pandoraCore$cacheName) {
        instances.remove(pandoraCore$cacheName);
    }

    private String getVertex(ResourceLocation template, boolean complete) {
        JsonObject obj = shaderJsons.get(template);
        String fName = obj.getAsJsonPrimitive("vertex").getAsString();
        ResourceLocation loc = new ResourceLocation(fName);
        List<String> res = sources.get(loc.getNamespace(), loc.getPath() + ".vsh");
        if (complete && res == null) {
            forceLoad = true;
            try {
                // TODO: ShaderInstance#getOrCreate
            } catch (Throwable err) {
                forceLoad = false;
                throw new RuntimeException(err);
            }
            forceLoad = false;

            res = sources.get(loc.getNamespace(), loc.getPath() + ".vsh");
        }
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    private String getFragment(ResourceLocation template, boolean complete) {
        JsonObject obj = shaderJsons.get(template);
        String fName = obj.getAsJsonPrimitive("fragment").getAsString();
        ResourceLocation loc = new ResourceLocation(fName);
        List<String> res = sources.get(loc.getNamespace(), loc.getPath() + ".fsh");
        if (complete && res == null) {
            forceLoad = true;
            try {
                // TODO: ShaderInstance#getOrCreate
            } catch (Throwable err) {
                forceLoad = false;
                throw new RuntimeException(err);
            }
            forceLoad = false;

            res = sources.get(loc.getNamespace(), loc.getPath() + ".fsh");
        }
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateTransformation transformation, boolean complete) {
        String template = transformation.getTemplate("vanilla");
        if (template == null)
            return LoadResult.FAILED;
        String templateJson = template + ".json";

        try {
            ResourceLocation loc = new ResourceLocation(template);
            ResourceLocation locJson = new ResourceLocation(templateJson);
            String vsh;
            String fsh;
            try {
                vsh = getVertex(locJson, complete);
                fsh = getFragment(locJson, complete);
            } catch (Throwable err) {
                return LoadResult.UNCACHED;
            }

            if (loc.getNamespace().equals("minecraft")) {
                template = loc.getPath().substring("shaders/core/".length());
            } else {
                template = loc.getNamespace() + ":" + loc.getPath().substring("shaders/core/".length());
            }
            ShaderInstance instance = instances.get(template);
            if (vsh == null || fsh == null || instance == null)
                return LoadResult.UNCACHED;

            ShaderFile file = processor.process(ShaderParser.parse(vsh), transformation);
            vsh = file.toString();
            manager.load(new VanillaTemplatedShader(
                    this, transformation,
                    template, instance,
                    vsh, fsh
            ));

            return LoadResult.LOADED;
        } catch (Throwable err) {
            LOGGER.error("Failed loading template template " + transformation.location + " for shader " + template, err);
            return LoadResult.FAILED;
        }
    }

    @Override
    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateTransformation transformation) {
        return attempt(manager, transformation, false);
    }

    @Override
    public boolean attemptComplete(TemplateManager.LoadManager manager, TemplateTransformation transformation) {
        return attempt(manager, transformation, true) == LoadResult.LOADED;
    }

    @Override
    public String name() {
        return "vanilla";
    }

    @Override
    public TransformationProcessor processor() {
        return processor;
    }
}

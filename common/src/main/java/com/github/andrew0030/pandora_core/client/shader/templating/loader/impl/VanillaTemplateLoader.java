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

    public VanillaTemplateLoader(Map<ResourceLocation, JsonObject> shaderJsons) {
        this.shaderJsons = shaderJsons;
    }

    public static void activeFile(String source, String file) {
        MOD = source;
        ACTIVE = file;
        SOURCE = new ArrayList<>();
    }

    public static void shaderSource(List<String> $$1) {
        SOURCE.addAll($$1);
    }

    public static void link() {
        sources.put(MOD, ACTIVE, new ReadOnlyList<>(SOURCE));
        TemplateManager.reloadTemplate("vanilla", MOD, ACTIVE);
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Vanilla");

    private static final Map<String, ShaderInstance> instances = new HashMap<>();

    public static void bindShader(String $$1, ShaderInstance shaderInstance) {
        instances.put($$1, shaderInstance);
    }

    private String getVertex(ResourceLocation template) {
        JsonObject obj = shaderJsons.get(template);
        String fName = obj.getAsJsonPrimitive("vertex").getAsString();
        ResourceLocation loc = new ResourceLocation(fName);
        List<String> res = sources.get(loc.getNamespace(), loc.getPath() + ".vsh");
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    private String getFragment(ResourceLocation template) {
        JsonObject obj = shaderJsons.get(template);
        String fName = obj.getAsJsonPrimitive("fragment").getAsString();
        ResourceLocation loc = new ResourceLocation(fName);
        List<String> res = sources.get(loc.getNamespace(), loc.getPath() + ".fsh");
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    @Override
    public boolean attempt(TemplateManager.LoadManager manager, TemplateTransformation transformation) {
        String template = transformation.getTemplate("vanilla") + ".json";
        if (template == null)
            return false;

        try {
            ResourceLocation loc = new ResourceLocation(template);
            String vsh = getVertex(loc);
            String fsh = getFragment(loc);
            ShaderFile file = processor.process(ShaderParser.parse(vsh), transformation);
            vsh = file.toString();
            manager.load(new VanillaTemplatedShader(
                    this, transformation,
                    template, instances.get(template),
                    vsh, fsh
            ));

            return true;
        } catch (Throwable err) {
            LOGGER.error("Failed loading template template " + transformation.location + " for shader " + template, err);
            return false;
        }
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

package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VanillaTemplateLoader extends TemplateLoader {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

    private final List<JsonObject> shaderJsons;
    private final TransformationProcessor processor = new DefaultTransformationProcessor();

    public VanillaTemplateLoader(List<JsonObject> shaderJsons) {
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

    @Override
    public boolean attempt(TemplateTransformation transformation) {
        return false;
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

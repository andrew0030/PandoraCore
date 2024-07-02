package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VanillaTemplateLoader {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

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
}

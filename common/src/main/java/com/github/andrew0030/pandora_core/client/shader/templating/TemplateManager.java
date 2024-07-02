package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShader;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.HashMap;

public class TemplateManager {
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
}

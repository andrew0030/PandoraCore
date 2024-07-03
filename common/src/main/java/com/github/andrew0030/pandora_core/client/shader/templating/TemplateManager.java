package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.TemplatedShader;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.ArrayList;
import java.util.HashMap;

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

    private static final ArrayList<TemplateTransformation> TRANSFORMATIONS = new ArrayList<>();

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
    }

    public void register(TemplateTransformation transformation) {
        TRANSFORMATIONS.add(transformation);
    }

    public void reload() {
        // TODO
    }
}

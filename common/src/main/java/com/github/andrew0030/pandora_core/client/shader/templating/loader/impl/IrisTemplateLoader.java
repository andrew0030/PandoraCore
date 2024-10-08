package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.NameMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.VanillaTemplatedShader;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderFile;
import com.github.andrew0030.pandora_core.client.utils.shader.ShaderParser;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class IrisTemplateLoader extends TemplateLoader implements VariableMapper {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

    private final TransformationProcessor processor = new DefaultTransformationProcessor();

    private static IrisTemplateLoader INSTANCE;

    public IrisTemplateLoader() {
        if (INSTANCE != null)
            throw new RuntimeException("Cannot create two vanilla template loaders.");
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
        if (MOD != null)
            sources.put(MOD, ACTIVE, new ReadOnlyList<>(SOURCE));
    }

    public static void cancel() {
        MOD = null;
        ACTIVE = null;
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Iris");

    private static final Map<String, ShaderInstance> instances = new HashMap<>();

    public static void bindShader(String $$1, ShaderInstance shaderInstance) {
        instances.put($$1, shaderInstance);
        if (!forceLoad)
            TemplateManager.reloadTemplate(INSTANCE, $$1);
    }

    public static void unbindShader(String pandoraCore$cacheName, ShaderInstance instance) {
        instances.remove(pandoraCore$cacheName, instance);
    }

    private String getVertex(String template, boolean complete, String[] names) {
        List<String> res = sources.get("minecraft", template + ".vsh");
        names[0] = template;
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    private String getFragment(String template, boolean complete, String[] names) {
        List<String> res = sources.get("minecraft", template + ".fsh");
        names[1] = template;
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        return out.toString();
    }

    @Override
    public boolean matches(TemplatedShader direct, String shader, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        TemplateShaderResourceLoader.TemplateStruct transformation = direct.transformation();
        String plate = transformation.getTemplate("iris");
        if (plate == null)
            return false;

        ResourceLocation loc = new ResourceLocation(shader);
        String mod = loc.getNamespace();
        String active = loc.getPath();

        if (mod.equals("minecraft") && active.equals(plate)) {
//            if (direct instanceof VanillaTemplatedShader vts)
//                return vts.matches("minecraft", plate + ".fsh");
            return true;
        }
        return false;
    }

    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, boolean complete, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        String template = struct.getTemplate("iris");
        if (template == null)
            return LoadResult.FAILED;

        TemplateTransformation transformation = struct.getTransformation("vsh", transformers, transformations);

        try {
            String[] names = new String[2];
            String vsh;
            String fsh;
            try {
                vsh = getVertex(template, complete, names);
                fsh = getFragment(template, complete, names);
            } catch (Throwable err) {
                return LoadResult.UNCACHED;
            }

            ShaderInstance instance = instances.get(template);
            if (vsh == null || fsh == null || instance == null)
                return LoadResult.UNCACHED;

            ShaderFile file = processor.process(this, ShaderParser.parse(vsh), transformation);
            vsh = file.toString();
            manager.load(new VanillaTemplatedShader(
                    this, this,
                    struct,
                    template, instance,
                    vsh, fsh,
                    "minecraft:" + names[0], "minecraft:" + names[1]
            ));

            return LoadResult.LOADED;
        } catch (Throwable err) {
            LOGGER.error("Failed loading template template " + transformation.location + " for shader " + template, err);
            return LoadResult.FAILED;
        }
    }

    @Override
    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, false, transformers, transformations);
    }

    @Override
    public boolean attemptComplete(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, true, transformers, transformations) == LoadResult.LOADED;
    }

    @Override
    public String name() {
        return "iris";
    }

    @Override
    public TransformationProcessor processor() {
        return processor;
    }

    @Override
    public void beginReload() {
        sources.clear();
    }

    @Override
    public String mapFrom(String proposedType, String srcName) {
        return NameMapper.fromIris(proposedType, srcName);
    }

    @Override
    public String mapTo(String proposedType, String name) {
        return NameMapper.toIris(proposedType, name);
    }
}

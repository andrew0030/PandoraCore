package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.VanillaTemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentType;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class VanillaTemplateLoader extends TemplateLoader implements VariableMapper {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

    private final Map<ResourceLocation, JsonObject> shaderJsons = new HashMap<>();
    private final TransformationProcessor processor = new DefaultTransformationProcessor();

    private static VanillaTemplateLoader INSTANCE;

    public VanillaTemplateLoader() {
        super(ShaderCapabilities.CAPABILITIES_ALL_VANILLA);
        if (INSTANCE != null)
            throw new RuntimeException("Cannot create two vanilla template loaders.");
        INSTANCE = this;
    }

    @Override
    public void prepare(ResourceManager manager) {
        manager.listResources(
                "shaders/core",
                (location) -> location.getPath().endsWith(".json")
        ).forEach((location, resource) -> {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = resource.openAsReader()) {
                reader.lines().forEach(line -> builder.append(line).append("\n"));
                shaderJsons.put(location, TemplateManager.GSON.fromJson(builder.toString(), JsonObject.class));
            } catch (Throwable err) {
                LOGGER.warn("Failed to parse vanilla json " + location.toString(), err);
            }
        });
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

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Vanilla");

    private static final Map<String, ShaderInstance> instances = new HashMap<>();

    public static void bindShader(String $$1, ShaderInstance shaderInstance) {
        instances.put($$1, shaderInstance);
        if (!forceLoad)
            TemplateManager.reloadTemplate(INSTANCE, $$1);
    }

    public static void unbindShader(String pandoraCore$cacheName, ShaderInstance instance) {
        instances.remove(pandoraCore$cacheName, instance);
    }

    @Override
    public boolean matches(TemplatedShader direct, String shader, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        TemplateShaderResourceLoader.TemplateStruct transformation = direct.transformation();
        String plate = transformation.getTemplate("vanilla");
        if (plate == null)
            return false;

        ResourceLocation loc = new ResourceLocation(shader);
        String mod = loc.getNamespace();
        String active = loc.getPath();

        if (!plate.equals(mod + ":" + active))
            return false;

        if (direct instanceof VanillaTemplatedShader vts) {
//            JsonObject obj = shaderJsons.get(new ResourceLocation(plate + ".json"));
//            if (obj == null)
//                return false;
//
//            String vsh = obj.getAsJsonPrimitive("vertex").getAsString();
//            String fsh = obj.getAsJsonPrimitive("fragment").getAsString();
//
//            return vts.matches(mod, vsh) ||
//                    vts.matches(mod, fsh);
            return true;
        }
        return false;
    }

    private void getVertex(ResourceLocation template, boolean complete, AttachmentSpecifier[] specifiers) {
        JsonObject obj = shaderJsons.get(template);
        String fName = obj.getAsJsonPrimitive("vertex").getAsString();
        ResourceLocation loc = new ResourceLocation(fName);
        List<String> res = sources.get(loc.getNamespace(), loc.getPath() + ".vsh");
        if (complete && res == null) {
            forceLoad = true;
            try {
                // TODO: check
//                ShaderInstance.getOrCreate(
//                        Minecraft.getInstance().getResourceManager(),
//                        Program.Type.VERTEX,
//                        loc.toString()
//                );
            } catch (Throwable err) {
                forceLoad = false;
                throw new RuntimeException(err);
            }
            forceLoad = false;

            res = sources.get(loc.getNamespace(), loc.getPath() + ".vsh");
        }
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        specifiers[0] = new AttachmentSpecifier(
                AttachmentType.VERTEX, out.toString(),
                template.toString()
        );
    }

    private void getFragment(ResourceLocation template, boolean complete, AttachmentSpecifier[] specifiers) {
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
        specifiers[1] = new AttachmentSpecifier(
                AttachmentType.FRAGMENT, out.toString(),
                template.toString()
        );
    }

    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, boolean complete,  Function<String, TemplateTransformation> transformations) {
        Map<String, String> transformers = struct.getTransformers();

        String template = struct.getTemplate("vanilla");
        if (template == null)
            return LoadResult.FAILED;
        String templateJson = template + ".json";

        TemplateTransformation transformation = struct.getTransformation("vsh", transformers, transformations);

        try {
            ResourceLocation loc = new ResourceLocation(template);
            ResourceLocation locJson = new ResourceLocation(templateJson);

            AttachmentSpecifier[] specifiers = new AttachmentSpecifier[5];
            try {
                getVertex(locJson, complete, specifiers);
                getFragment(locJson, complete, specifiers);
            } catch (Throwable err) {
                return LoadResult.UNCACHED;
            }

            if (loc.getNamespace().equals("minecraft")) {
                template = loc.getPath().substring("shaders/core/".length());
            } else {
                template = loc.getNamespace() + ":" + loc.getPath().substring("shaders/core/".length());
            }
            ShaderInstance instance = instances.get(template);
            if (specifiers[0] == null || specifiers[1] == null || instance == null)
                return LoadResult.UNCACHED;

            manager.load(new VanillaTemplatedShader(
                    this, this,
                    transformers, transformations,
                    struct, processor,
                    template, instance,
                    specifiers
            ));

            return LoadResult.LOADED;
        } catch (Throwable err) {
            LOGGER.error("Failed loading template template " + struct.location + " for shader " + template, err);
            return LoadResult.FAILED;
        }
    }

    @Override
    public String mapFrom(String proposedType, String name) {
        return VariableMapper.super.mapFrom(proposedType, name);
    }

    @Override
    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, false, transformations);
    }

    @Override
    public boolean attemptComplete(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, true, transformations) == LoadResult.LOADED;
    }

    @Override
    public String name() {
        return "vanilla";
    }

    @Override
    public TransformationProcessor processor() {
        return processor;
    }

    @Override
    public void _beginReload() {
        sources.clear();
        shaderJsons.clear();
    }
}

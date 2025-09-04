package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import com.github.andrew0030.pandora_core.utils.resource.ResourceHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class TemplateShaderResourceLoader implements PacoResourceManager {
    public TemplateShaderResourceLoader() {
    }

    private static final TemplateManager templateManager = new TemplateManager();
    private static final TemplateTransformationParser parser = new TemplateTransformationParser();

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Loader");

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        // clear caches
        templateManager.beginReload();

        ResourceDispatcher.SubDispatch<Void> dispatch = null;
        for (TemplateLoader allLoader : templateManager.allLoaders()) {
            if (dispatch != null) {
                dispatch = dispatch.prepare(
                        "paco_prepare_shader_loader_" + allLoader.name(),
                        (v) -> {
                            allLoader.prepare(manager);
                        }
                );
            } else {
                dispatch = dispatcher.prepare(
                        "paco_prepare_shader_loader_" + allLoader.name(),
                        () -> allLoader.prepare(manager)
                );
            }
        }

        dispatch
                // load template shader transformations
                .prepare("paco_parse_template_shaders", (v) -> {
                    Map<String, TemplateTransformation> transformationMap = new HashMap<>();

                    manager.listResources(
                            "shaders/paco/templated",
                            (location) ->
                                    location.getPath().endsWith(".vsh") ||
                                            location.getPath().endsWith(".fsh") ||
                                            location.getPath().endsWith(".glsl")
                    ).forEach((location, resource) -> {
                        try {
                            TemplateTransformation transformation = parser.parse(location, ResourceHelper.readWholeResource(resource));
                            transformationMap.put(transformation.location.toString(), transformation);
                        } catch (Throwable err) {
                            LOGGER.warn("Failed to parse shader template " + location.toString(), err);
                        }
                    });

                    return transformationMap;
                })
                // load template shader jsons
                .prepare("paco_parse_template_shaders", (v) -> {
                    List<TemplateStruct> structs = new ArrayList<>();
                    manager.listResources(
                            "shaders/paco/templated",
                            (location) -> location.getPath().endsWith(".json")
                    ).forEach((location, resource) -> {
                        try {
                            JsonObject obj = TemplateManager.GSON.fromJson(
                                    ResourceHelper.readWholeResource(resource),
                                    JsonObject.class
                            );

                            // strip extension
                            String pth = location.getPath();
                            if (pth.endsWith(".json"))
                                pth = pth.substring(0, pth.length() - ".json".length());

                            TemplateStruct struct = new TemplateStruct(
                                    new ResourceLocation(
                                            location.getNamespace(),
                                            pth
                                    )
                            );

                            struct.parse(obj);
                            structs.add(struct);
                        } catch (Throwable err) {
                            LOGGER.warn("Failed to parse shader template " + location.toString(), err);
                        }
                    });
                    return Pair.of(v, structs);
                })
                // await preparation completion
                .barrier()
                // load shaders
                .apply("paco_load_template_shaders", (result) -> {
                    templateManager.preload(result.getFirst(), result.getSecond());
                });
    }

    public final class TemplateStruct {
        public final ResourceLocation location;
        private Map<String, String> templates = new HashMap<>();
        private Map<String, String> transformers = new HashMap<>();
        private List<String> instanceData = new ArrayList<>();
        private List<String> vertexAttributes = new ArrayList<>();
        private List<String> uniforms = new ArrayList<>();
        private HashMap<String, String> dataTypes = new HashMap<>();

        public TemplateStruct(ResourceLocation location) {
            this.location = location;
        }

        public void addInstanceData(String name) {
            instanceData.add(name);
        }

        public void addVertexAttrib(String name) {
            vertexAttributes.add(name);
        }

        public void lock() {
            vertexAttributes.addAll(0, instanceData);
            instanceData = new ReadOnlyList<>(instanceData);
            vertexAttributes = new ReadOnlyList<>(vertexAttributes);
            uniforms = new ReadOnlyList<>(uniforms);
        }

        public Map<String, String> getTemplates() {
            return templates;
        }

        public Map<String, String> getTransformers() {
            return transformers;
        }

        public List<String> getInstanceData() {
            return instanceData;
        }

        public List<String> getVertexAttributes() {
            return vertexAttributes;
        }

        public List<String> getUniforms() {
            return uniforms;
        }

        public String getTemplate(String of) {
            return templates.get(of);
        }

        public String getAttributeType(String name) {
            return dataTypes.get(name);
        }

        public void parse(JsonObject obj) {
            JsonObject tplate = obj.getAsJsonObject("template");
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : tplate.entrySet()) {
                this.templates.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }

            JsonObject tformer = obj.getAsJsonObject("transformer");
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : tformer.entrySet()) {
                this.transformers.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
            }

            JsonObject idata = obj.getAsJsonObject("instance_data");
            for (String s : idata.keySet()) {
                this.instanceData.add(s);
                this.dataTypes.put(s, idata.getAsJsonPrimitive(s).getAsString());
            }
        }

        public TemplateTransformation getTransformation(String variety, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
            return transformations.apply(transformers.get(variety));
        }
    }
}

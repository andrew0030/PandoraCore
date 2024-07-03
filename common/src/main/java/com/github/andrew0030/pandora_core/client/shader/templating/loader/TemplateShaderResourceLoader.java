package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformationParser;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.Reader;

public class TemplateShaderResourceLoader implements PacoResourceManager {
    public TemplateShaderResourceLoader() {
    }

    private static final TemplateManager templateManager = new TemplateManager();
    private static final TemplateTransformationParser parser = new TemplateTransformationParser();

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        templateManager.beginReload();

        dispatcher
                .prepare("paco_parse_template_shaders", () -> {
                    manager.listResources(
                            "shaders/paco/templated",
                            (location) -> location.getPath().endsWith(".glsl")
                    ).forEach((location, resource) -> {
                        StringBuilder builder = new StringBuilder();
                        try (BufferedReader reader = resource.openAsReader()) {
                            reader.lines().forEach(line -> builder.append(line).append("\n"));
                            TemplateTransformation transformation = parser.parse(builder.toString());
                            templateManager.register(transformation);
                        } catch (Throwable err) {
                            throw new RuntimeException(err);
                        }
                    });
                })
                .prepare("paco_get_shader_jsons", (v) -> {
                    manager.listResources(
                            "shaders/core",
                            (location) -> location.getPath().endsWith(".json")
                    ).forEach((location, resource) -> {
                        StringBuilder builder = new StringBuilder();
                        try (BufferedReader reader = resource.openAsReader()) {
                            reader.lines().forEach(line -> builder.append(line).append("\n"));
                            templateManager.addJson(builder.toString());
                        } catch (Throwable err) {
                            throw new RuntimeException(err);
                        }
                    });
                })
                .barrier()
                .apply("paco_load_template_shaders", (result) -> {
                    templateManager.reload();
                });
    }
}

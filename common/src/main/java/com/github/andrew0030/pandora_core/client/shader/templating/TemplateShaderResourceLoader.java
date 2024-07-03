package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.BufferedReader;

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

        dispatcher
                // load template shader files
                .prepare("paco_parse_template_shaders", () -> {
                    manager.listResources(
                            "shaders/paco/templated",
                            (location) -> location.getPath().endsWith(".glsl")
                    ).forEach((location, resource) -> {
                        StringBuilder builder = new StringBuilder();
                        try (BufferedReader reader = resource.openAsReader()) {
                            reader.lines().forEach(line -> builder.append(line).append("\n"));
                            TemplateTransformation transformation = parser.parse(location, builder.toString());
                            templateManager.register(transformation);
                        } catch (Throwable err) {
                            LOGGER.warn("Failed to parse shader template " + location.toString(), err);
                        }
                    });
                })
                // load the vanilla shader jsons into a cache
                .prepare("paco_get_shader_jsons", (v) -> {
                    manager.listResources(
                            "shaders/core",
                            (location) -> location.getPath().endsWith(".json")
                    ).forEach((location, resource) -> {
                        StringBuilder builder = new StringBuilder();
                        try (BufferedReader reader = resource.openAsReader()) {
                            reader.lines().forEach(line -> builder.append(line).append("\n"));
                            templateManager.addJson(location, builder.toString());
                        } catch (Throwable err) {
                            LOGGER.warn("Failed to parse vanilla json " + location.toString(), err);
                        }
                    });
                 })
                // await preparation completion
                .barrier()
                // load shaders
                .apply("paco_load_template_shaders", (result) -> {
                    templateManager.reload();
                });
    }
}

package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformationParser;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;

public class TemplateShaderResourceLoader implements PacoResourceManager {
    public TemplateShaderResourceLoader() {
    }

    private static final TemplateManager templateManager = new TemplateManager();
    private static final TemplateTransformationParser parser = new TemplateTransformationParser();

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        dispatcher
                .prepare("paco_parse_template_shaders", () -> {
                    templateManager.beginReload();

                    manager.listResources(
                            "shaders/paco/templated",
                            (location) -> location.getPath().endsWith(".glsl")
                    ).forEach((location, resource) -> {
                        StringBuilder builder = new StringBuilder();
                        try {
                            resource.openAsReader().lines().forEach(line -> {
                                builder.append(line).append("\n");
                            });
                        } catch (Throwable err) {
                            throw new RuntimeException(err);
                        }
                        TemplateTransformation transformation = parser.parse(builder.toString());

                        templateManager.register(transformation);
                    });
                })
                .barrier()
                .apply("paco_load_template_shaders", (result) -> {
                    templateManager.reload();
                });
    }
}

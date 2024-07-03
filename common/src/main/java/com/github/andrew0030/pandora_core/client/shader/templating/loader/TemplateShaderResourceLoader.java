package com.github.andrew0030.pandora_core.client.shader.templating.loader;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformationParser;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TemplateShaderResourceLoader implements ResourceManagerReloadListener {
    public TemplateShaderResourceLoader() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }

    private static final TemplateManager templateManager = new TemplateManager();

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager manager,
            ProfilerFiller prepareProfiler,
            ProfilerFiller applyProfiler,
            Executor prepareExecutor,
            Executor applyExecutor
    ) {
        final TemplateTransformationParser parser = new TemplateTransformationParser();
        CompletableFuture<Void> prepDataOne = CompletableFuture.runAsync(() -> {
            prepareProfiler.push("paco_parse_template_shaders");
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
            prepareProfiler.pop();
        }, prepareExecutor);

        return prepDataOne.thenCompose(barrier::wait).thenRun(() -> {
            prepareProfiler.push("paco_load_template_shaders");
            templateManager.reload();
            prepareProfiler.pop();
        });
    }
}

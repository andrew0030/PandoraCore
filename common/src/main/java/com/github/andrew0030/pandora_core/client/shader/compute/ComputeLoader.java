package com.github.andrew0030.pandora_core.client.shader.compute;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.resource.PacoResourceManager;
import com.github.andrew0030.pandora_core.utils.resource.ResourceDispatcher;
import com.github.andrew0030.pandora_core.utils.resource.ResourceHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ComputeLoader implements PacoResourceManager {
    public ComputeLoader() {
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Compute Shaders", "Loader");
    private Map<ResourceLocation, ComputeShader> shaders = new HashMap<>();

    @Override
    public void run(ResourceManager manager, ResourceDispatcher dispatcher) {
        dispatcher
                .prepare(
                        "paco_collect_compute_shaders",
                        () -> {
                            Map<ResourceLocation, String> shaderSources = new HashMap<>();
                            Map<ResourceLocation, Resource> resources = manager.listResources(
                                    "shaders/paco/compute",
                                    (loc) -> loc.getPath().endsWith(".glsl")
                            );
                            resources.forEach((k, v) -> {
                                try {
                                    shaderSources.put(k, ResourceHelper.readWholeResource(v));
                                } catch (Throwable err) {
                                    LOGGER.warn("... what?", err);
                                }
                            });
                            return shaderSources;
                        }
                ).barrier()
                .apply("paco_load_compute_shaders", (map) -> {
                    map.forEach((k, v) -> {
                        shaders.put(
                                k, new ComputeShader(k, v)
                        );
                    });
                });
    }
}

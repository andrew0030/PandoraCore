package com.github.andrew0030.pandora_core;

import com.github.andrew0030.pandora_core.client.PaCoClientManager;
import com.github.andrew0030.pandora_core.client.registry.PaCoCoreShaders;
import com.github.andrew0030.pandora_core.client.registry.PaCoKeyMappings;
import com.github.andrew0030.pandora_core.client.registry.PaCoModelLayers;
import com.github.andrew0030.pandora_core.client.registry.PaCoPostShaders;
import com.github.andrew0030.pandora_core.client.render.renderers.registry.InstancedBERendererRegistry;
import com.github.andrew0030.pandora_core.client.shader.compute.ComputeLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.registry.test.PaCoBlockEntities;
import com.github.andrew0030.pandora_core.registry.test.PaCoBlocks;
import com.github.andrew0030.pandora_core.test.InstancingTestBlockEntityRenderer;
import com.github.andrew0030.pandora_core.test.PaCoBlockRenderTypes;
import com.github.andrew0030.pandora_core.test.PaCoColorHandlers;
import com.github.andrew0030.pandora_core.test.block_entities.TestBEWLR;
import com.github.andrew0030.pandora_core.test.entity.PaCoEntities;
import com.github.andrew0030.pandora_core.test.particle.PaCoParticleProviders;
import com.github.andrew0030.pandora_core.utils.LogicalSide;

import java.util.List;

public class PandoraCoreClient {
    public static final TemplateShaderResourceLoader templateShaderLoader = new TemplateShaderResourceLoader();
    public static final ComputeLoader computeShaderLoader = new ComputeLoader();
    public static final PaCoCoreShaders coreShaders = new PaCoCoreShaders();

    /** Early Client Init (Mod Construction) **/
    public static void earlyInit() {
        // Registers the PaCo KeyMappings
        PaCoKeyMappings.KEY_MAPPINGS.register();
        // Registers the PaCo ModelLayers
        PaCoModelLayers.MODEL_LAYERS.register();
        // Registers the PaCo ColorHandlers
        PaCoColorHandlers.COLOR_HANDLERS.register();
        // Registers the PaCo BlockRenderTypes
        PaCoBlockRenderTypes.RENDER_TYPES.register();
        // Registers the PaCo ParticleProviders
        PaCoParticleProviders.PARTICLE_PROVIDERS.register();

        PaCoTesting.testInitClient();

        Services.RELOAD_LISTENER.registerResourceLoader((side) -> {
            if (side == LogicalSide.CLIENT) {
                return List.of(templateShaderLoader, computeShaderLoader, coreShaders);
            } else return null;
        });
    }

    /** Client Init */
    public static void init() {
        PaCoPostShaders.init();
    }

    /** Thread Safe Client Init */
    public static void initThreadSafe() {
        // TODO: remove this when done with testing
        PaCoClientManager.registerItemBEWLR(PaCoBlocks.TEST.get(), TestBEWLR.INSTANCE);
        // Immediate Rendering
        PaCoBlockEntities.registerBlockEntityRenderers();
        // Instanced Rendering
        InstancedBERendererRegistry.register(PaCoBlockEntities.INSTANCING_TEST.get(), new InstancingTestBlockEntityRenderer());

        // Entities
        PaCoEntities.registerEntityRenderers();
    }
}
package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.model.PaCoModelLayerRegistry;
import com.github.andrew0030.pandora_core.test.block_entities.TestBEModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

//TODO remove this when done testing everything
public class PaCoModelLayers {
    public static final PaCoModelLayerRegistry MODEL_LAYERS = new PaCoModelLayerRegistry();

    public static final ModelLayerLocation TEST = MODEL_LAYERS.add(new ModelLayerLocation(new ResourceLocation(PandoraCore.MOD_ID, "test"), "main"), TestBEModel::createBodyLayer);
}
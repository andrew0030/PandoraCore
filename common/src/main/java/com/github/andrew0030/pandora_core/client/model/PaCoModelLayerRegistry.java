package com.github.andrew0030.pandora_core.client.model;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A simple, cross-platform registry helper used for registering {@link ModelLayerLocation ModelLayerLocations}
 * and their corresponding {@link LayerDefinition LayerDefinitions}, in a unified way across all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoModelLayerRegistry MODEL_LAYERS = new PaCoModelLayerRegistry();
 * public static final ModelLayerLocation EXAMPLE_LAYER = MODEL_LAYERS.add(
 *     new ModelLayerLocation(new ResourceLocation("example_mod", "example_model"), "main"),
 *     ExampleModel::createBodyLayer
 * );
 * }</pre>
 * <p>And then client side during mod construction:</p>
 * <pre>{@code
 * ExampleModModelLayers.MODEL_LAYERS.register();
 * }</pre>
 */
public class PaCoModelLayerRegistry {
    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers = new HashMap<>();

    /**
     * Adds a new {@link ModelLayerLocation} and its corresponding {@link LayerDefinition} supplier to be registered later.
     *
     * @param location   The {@link ModelLayerLocation} representing the unique identifier for the model layer.
     * @param definition A {@link Supplier} that provides the {@link LayerDefinition} for this model layer.
     * @return The given {@link ModelLayerLocation}, for convenient assignment.
     */
    public ModelLayerLocation add(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        this.modelLayers.put(location, definition);
        return location;
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Client side, inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside ClientModInitializer#onInitializeClient.<br/>
     */
    public void register() {
        Services.REGISTRY.registerModelLayers(this.modelLayers);
    }
}
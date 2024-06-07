package com.github.andrew0030.pandora_core.client.shader.holder;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * A holder class that allows registering a {@link PostChain} which initially remains null, until shaders are reloaded.
 * The class also holds information such as the:
 * <ul>
 *   <li>{@link ResourceLocation} of the shader.</li>
 *   <li>{@link IPaCoPostChainProcessor} which is used to handle passing parameters to the shader.</li>
 * </ul>
 * The holder also has a <b>processPostChain</b> method, which uses the given parameters and processes the shader.
 */
public class PostChainHolder {
    private final ResourceLocation resourceLocation;
    private final IPaCoPostChainProcessor processor;
    private PostChain postChain;

    public PostChainHolder(ResourceLocation resourceLocation, IPaCoPostChainProcessor processor) {
        this.resourceLocation = resourceLocation;
        this.processor = processor;
        this.postChain = null;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public void processPostChain(float partialTick, Map<String, Object> parameters) {
        if (this.postChain != null) {
            this.processor.process(this.postChain, partialTick, parameters);
        }
    }

    public PostChain getPostChain() {
        return this.postChain;
    }

    public void setPostChain(PostChain postChain) {
        this.postChain = postChain;
    }
}
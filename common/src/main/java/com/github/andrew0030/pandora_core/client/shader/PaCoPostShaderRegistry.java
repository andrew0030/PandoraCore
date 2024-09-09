package com.github.andrew0030.pandora_core.client.shader;

import com.github.andrew0030.pandora_core.client.shader.holder.IPaCoPostChainProcessor;
import com.github.andrew0030.pandora_core.client.shader.holder.PostChainHolder;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/** Helper Class that allows registering PostChains. */
public class PaCoPostShaderRegistry {
    public static final List<PostChainHolder> POST_SHADERS = new ArrayList<>();

    /**
     * Used to register a custom {@link PostChain}.
     * @param path The {@link ResourceLocation} pointing to the shaders json file.
     * @param processor A Functional Interface that allows using any number of parameters.
     * @return A {@link PostChainHolder}, containing the {@link ResourceLocation}, {@link PostChain} and a processor method.
     */
    public static PostChainHolder register(ResourceLocation path, IPaCoPostChainProcessor processor) {
        PostChainHolder holder = new PostChainHolder(path, processor);
        POST_SHADERS.add(holder);
        return holder;
    }
}
package com.github.andrew0030.pandora_core.client.shader.holder;

import com.github.andrew0030.pandora_core.mixin_interfaces.IPaCoUniformAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
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
 * The holder also has a {@link com.github.andrew0030.pandora_core.client.shader.holder.PostChainHolder#processPostChain(float, Map)} method, which uses the given parameters and processes the shader.
 */
public class PostChainHolder {
    private final ResourceLocation resourceLocation;
    private final IPaCoPostChainProcessor processor;
    private PostChain postChain;
    private final Map<String, PaCoUniformHolder> uniforms;

    public PostChainHolder(ResourceLocation resourceLocation, IPaCoPostChainProcessor processor) {
        this.resourceLocation = resourceLocation;
        this.processor = processor;
        this.postChain = null;
        this.uniforms = new Object2ObjectAVLTreeMap<>();
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
        for (PaCoUniformHolder value : this.uniforms.values()) {
            value.isDirty = true;
        }
    }

    public PaCoUniformHolder getUniform(String name) {
        PaCoUniformHolder holder = this.uniforms.get(name);
        if (holder == null)
            this.uniforms.put(name, holder = new PaCoUniformHolder((IPaCoUniformAccess) postChain, name));
        if (holder.isDirty)
            holder.value.uniforms = ((IPaCoUniformAccess) postChain).getPaCoUniforms(name);
        return holder;
    }
}
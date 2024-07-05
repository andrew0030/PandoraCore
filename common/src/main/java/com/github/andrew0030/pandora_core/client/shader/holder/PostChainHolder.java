package com.github.andrew0030.pandora_core.client.shader.holder;

import com.github.andrew0030.pandora_core.mixin_interfaces.shader.post.IPaCoUniformAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.CheckForNull;
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
    private final Map<String, PaCoUniformHolder> uniforms;
    private PostChain postChain;

    public PostChainHolder(ResourceLocation resourceLocation, IPaCoPostChainProcessor processor) {
        this.resourceLocation = resourceLocation;
        this.processor = processor;
        this.uniforms = new Object2ObjectAVLTreeMap<>();
        this.postChain = null;
    }

    /** @return The {@link ResourceLocation} of this {@link PostChain}. */
    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    /** @return The {@link PostChain} of this {@link PostChainHolder}. */
    @CheckForNull
    public PostChain getPostChain() {
        return this.postChain;
    }

    /**
     * Called to process the {@link PostChain}.
     * @param partialTick The games current partial tick.
     * @param parameters A Map of Objects, this can be used to pass data to your PostShader, to use during registration.
     */
    public void processPostChain(float partialTick, Map<String, Object> parameters) {
        if (this.postChain != null) {
            this.processor.process(this.postChain, partialTick, parameters);
        }
    }

    /**
     * Called in {@link GameRenderer#reloadShaders(ResourceProvider)}, to create a new {@link PostChain} based on the given {@link ResourceLocation}.
     * If the {@link PostChain} is successfully created, the {@link PostChain} field of the {@link PostChainHolder} is updated.
     * */
    @ApiStatus.Internal
    public void setPostChain(PostChain postChain) {
        this.postChain = postChain;
        for (PaCoUniformHolder value : this.uniforms.values()) {
            value.isDirty = true;
        }
    }

    public PaCoUniformHolder getUniform(String name) {
        PaCoUniformHolder holder = this.uniforms.get(name);
        if (holder == null)
            this.uniforms.put(name, holder = new PaCoUniformHolder((IPaCoUniformAccess) this.postChain, name));
        if (holder.isDirty)
            holder.value.uniforms = ((IPaCoUniformAccess) postChain).pandoraCore$getUniforms(name);
        return holder;
    }
}
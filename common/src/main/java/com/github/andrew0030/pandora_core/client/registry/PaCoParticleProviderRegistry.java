package com.github.andrew0030.pandora_core.client.registry;

import com.github.andrew0030.pandora_core.platform.Services;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple, cross-platform registry helper used for registering {@link ParticleProvider ParticleProviders},
 * in a unified way for all mod loaders.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public static final PaCoParticleProviderRegistry PARTICLE_PROVIDERS = new PaCoParticleProviderRegistry();
 * static {
 *     PARTICLE_PROVIDERS.add(ExampleModParticles.EXAMPLE.get(), ExampleParticle.Provider::new);
 * }
 * }</pre>
 *
 * <p>And then client side during mod construction:</p>
 * <pre>{@code
 * ExampleModParticleProviders.PARTICLE_PROVIDERS.register();
 * }</pre>
 */
public class PaCoParticleProviderRegistry {
    private final Map<ParticleType<?>, ParticleProvider<?>> particleProviders = new HashMap<>();
    private final Map<ParticleType<?>, PendingParticleProvider<?>> pendingParticleProviders = new HashMap<>();

    /**
     * Registers a {@link ParticleProvider} for a {@link ParticleType} that does <strong>not</strong> use a JSON-defined {@link SpriteSet}.
     * <p>
     * This {@link ParticleType} <strong>must not</strong> have a corresponding JSON definition
     * under {@code assets/<modid>/particles/}, or a texture conflict will occur at runtime.
     * </p>
     *
     * @param particleType The {@link ParticleType} to associate with the {@link ParticleProvider}.
     * @param provider     The {@link ParticleProvider} instance.
     */
    public <T extends ParticleOptions> void add(ParticleType<T> particleType, ParticleProvider<T> provider) {
        this.particleProviders.put(particleType, provider);
    }

    /**
     * Registers a {@link PendingParticleProvider} for a {@link ParticleType}
     * that <strong>uses</strong> a JSON-defined {@link SpriteSet}.
     * <p>
     * These particles require a corresponding JSON file located at {@code assets/<modid>/particles/<id>.json},
     * which specifies the texture sprite(s) used for rendering. The provider will receive a {@link SpriteSet}
     * resolved from this JSON when registered.
     * </p>
     *
     * @param particleType     The {@link ParticleType} to associate with the {@link ParticleProvider}.
     * @param pendingProvider  The functional interface that produces a {@link ParticleProvider}, given a {@link SpriteSet}.
     */
    public <T extends ParticleOptions> void add(ParticleType<T> particleType, PendingParticleProvider<T> pendingProvider) {
        this.pendingParticleProviders.put(particleType, pendingProvider);
    }

    /**
     * This needs to be called, so event listeners are created by the loaders.<br/>
     * Here is a list of when to call it, on each loader:<br/><br/>
     * <strong>Forge</strong>: Client side, inside mod constructor.<br/>
     * <strong>Fabric</strong>: Inside {@code ClientModInitializer#onInitializeClient}.<br/>
     */
    public void register() {
        Services.REGISTRY.registerParticleProviders(this.particleProviders, this.pendingParticleProviders);
    }

    /**
     * A pending {@link ParticleProvider}.
     * @param <T> The type of particle effects this provider deals with.
     */
    @FunctionalInterface
    public interface PendingParticleProvider<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet spriteSet);
    }
}
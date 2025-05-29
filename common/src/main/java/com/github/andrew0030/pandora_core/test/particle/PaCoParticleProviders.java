package com.github.andrew0030.pandora_core.test.particle;

import com.github.andrew0030.pandora_core.client.particle.StarTestParticle;
import com.github.andrew0030.pandora_core.client.registry.PaCoParticleProviderRegistry;

public class PaCoParticleProviders {
    public static final PaCoParticleProviderRegistry PARTICLE_PROVIDERS = new PaCoParticleProviderRegistry();

    static {
        PARTICLE_PROVIDERS.add(PaCoParticles.STAR_TEST.get(), StarTestParticle.Provider::new);
    }
}
package com.github.andrew0030.pandora_core.test.particle;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.registry.PaCoRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class PaCoParticles {
    public static final PaCoRegistry<ParticleType<?>> PARTICLE_TYPES = new PaCoRegistry<>(BuiltInRegistries.PARTICLE_TYPE, PandoraCore.MOD_ID);

    public static final Supplier<SimpleParticleType> STAR_TEST = PARTICLE_TYPES.add("star_test", () -> new SimpleParticleType(false));
}
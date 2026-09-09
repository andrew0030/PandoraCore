package com.github.andrew0030.pandora_core.mixin.accessor;


import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// TODO: all non-static handlers should be prefixed as: modid$getFieldOrMethodName
@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor("generationSettings")
    @Mutable
    void setGenerationSettings(BiomeGenerationSettings generationSettings);

    @Accessor("mobSettings")
    @Mutable
    void setMobSettings(MobSpawnSettings mobSettings);
}
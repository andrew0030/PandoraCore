package com.github.andrew0030.pandora_core.mixin.accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(BiomeGenerationSettings.class)
public interface BiomeGenerationSettingsAccessor {
    @Invoker("<init>")
    static BiomeGenerationSettings createBiomeGenerationSettings(Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers, List<HolderSet<PlacedFeature>> features) {
        throw new AssertionError();
    }

    @Accessor
    Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> getCarvers();
}
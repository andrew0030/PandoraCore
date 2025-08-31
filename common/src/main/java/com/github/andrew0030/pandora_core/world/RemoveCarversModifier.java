package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.BiomeGenerationSettingsAccessor;
import com.github.andrew0030.pandora_core.utils.PaCoCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

import java.util.*;
import java.util.stream.Collectors;

//TODO write javadocs
public record RemoveCarversModifier(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, Set<GenerationStep.Carving> steps) implements Modifier {
    private static final EnumSet<GenerationStep.Carving> ALL_STEPS = EnumSet.allOf(GenerationStep.Carving.class);
    public static final Codec<RemoveCarversModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveCarversModifier::biomes),
            ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(RemoveCarversModifier::carvers),
            PaCoCodecUtils.singleOrSet(GenerationStep.Carving.CODEC).optionalFieldOf("steps", ALL_STEPS).forGetter(RemoveCarversModifier::steps)
        ).apply(instance, RemoveCarversModifier::new)
    );

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public Phase phase() {
        return Phase.REMOVE;
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomeHolders = this.biomes().stream().toList();
        for (Holder<Biome> holder : biomeHolders) {
            this.removeFromBiome(holder.value());
        }
    }

    private void removeFromBiome(Biome biome) {
        // Gets current biome generation settings and a copy of the internal map
        BiomeGenerationSettings settings = biome.getGenerationSettings();
        Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> biomeCarvers = new HashMap<>(((BiomeGenerationSettingsAccessor) settings).getCarvers());

        // Builds a Set of carvers to quickly check if they are present
        Set<Holder<ConfiguredWorldCarver<?>>> toRemove = this.carvers().stream().collect(Collectors.toCollection(HashSet::new));
        if (toRemove.isEmpty()) return;

        // A little smt to keep track of whether any changes happened
        boolean changed = false;

        // Iterates through the specified steps
        for (GenerationStep.Carving step : this.steps()) {
            HolderSet<ConfiguredWorldCarver<?>> existing = biomeCarvers.get(step);
            if (existing == null) continue; // If the category has no entries we skip further logic

            List<Holder<ConfiguredWorldCarver<?>>> base = existing.stream().toList();

            // If none of the base entries are in the removal set, we skip further logic
            boolean anyRemoved = false;
            for (Holder<ConfiguredWorldCarver<?>> holder : base) {
                if (toRemove.contains(holder)) {
                    anyRemoved = true;
                    break;
                }
            }
            if (!anyRemoved) continue;

            // If there are entries that need to be removed, we build a filtered list
            List<Holder<ConfiguredWorldCarver<?>>> filtered = new ArrayList<>(base.size());
            for (Holder<ConfiguredWorldCarver<?>> holder : base) {
                if (!toRemove.contains(holder))
                    filtered.add(holder);
            }

            // If filtered is empty rather than adding an empty holder set, this removes the step entirely
            biomeCarvers.compute(step, (key, value) -> filtered.isEmpty() ? null : HolderSet.direct(filtered));
            changed = true;
        }

        // If nothing changed, the biome generation settings don't need to be updated
        if (!changed) return;

        /*
         * Updates the generation settings of the Biome
         *
         * NOTE: The reason we are creating a new BiomeGenerationSettings instance,
         * is because internally during init BiomeGenerationSettings creates a few Sets
         * based on the given Carvers, which wouldn't be updated by changing one field.
         * Now technically this could also be done by manually updating all of those fields,
         * however that is likely more fragile, so in this case this approach is cleaner.
         */
        ((BiomeAccessor) (Object) biome).setGenerationSettings(
            // Creates a new BiomeGenerationSettings instance with the specified features removed
            BiomeGenerationSettingsAccessor.createBiomeGenerationSettings(
                biomeCarvers,
                settings.features()
            )
        );
    }
}
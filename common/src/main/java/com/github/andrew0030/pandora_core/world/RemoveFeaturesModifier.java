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
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link RemoveFeaturesModifier} can be used to remove {@link PlacedFeature} instances from {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:remove_features",
 *   "biomes": "minecraft:ocean",
 *   "features": "minecraft:kelp",
 *   "steps": "vegetal_decoration" // This is optional, if omitted it defaults to all steps
 * }
 * }</pre>
 *
 * @param biomes   The {@link Biome} instances the {@code features} will be removed from
 * @param features The {@link PlacedFeature} instances that will be removed
 * @param steps    The {@link GenerationStep.Decoration} steps from which {@code features} will be removed
 */
public record RemoveFeaturesModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<GenerationStep.Decoration> steps) implements Modifier {
    private static final EnumSet<GenerationStep.Decoration> ALL_STEPS = EnumSet.allOf(GenerationStep.Decoration.class);
    public static final Codec<RemoveFeaturesModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveFeaturesModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeaturesModifier::features),
            PaCoCodecUtils.singleOrSet(GenerationStep.Decoration.CODEC).optionalFieldOf("steps", ALL_STEPS).forGetter(RemoveFeaturesModifier::steps)
        ).apply(instance, RemoveFeaturesModifier::new)
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
        // Gets current biome generation settings and a copy of the internal list
        BiomeGenerationSettings settings = biome.getGenerationSettings();
        List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(settings.features());

        // Builds a Set of features to quickly check if they are present
        Set<Holder<PlacedFeature>> toRemove = this.features().stream().collect(Collectors.toCollection(HashSet::new));
        if (toRemove.isEmpty()) return;

        // A little smt to keep track of whether any changes happened
        boolean changed = false;

        // Iterates through the specified steps
        for (GenerationStep.Decoration step : this.steps()) {
            int index = step.ordinal();
            // If the biome doesn't have the step, there is nothing to remove
            if (index >= biomeFeatures.size()) continue;

            List<Holder<PlacedFeature>> base = biomeFeatures.get(index).stream().toList();

            // If none of the base entries are in the removal set, we skip further logic
            boolean anyRemoved = false;
            for (Holder<PlacedFeature> holder : base) {
                if (toRemove.contains(holder)) {
                    anyRemoved = true;
                    break;
                }
            }
            if (!anyRemoved) continue;

            // If there are entries that need to be removed, we build a filtered list
            List<Holder<PlacedFeature>> filtered = new ArrayList<>(base.size());
            for (Holder<PlacedFeature> holder : base) {
                if (!toRemove.contains(holder))
                    filtered.add(holder);
            }

            biomeFeatures.set(index, HolderSet.direct(filtered));
            changed = true;
        }

        // If nothing changed, the biome generation settings don't need to be updated
        if (!changed) return;

        /*
         * Updates the generation settings of the Biome
         *
         * NOTE: The reason we are creating a new BiomeGenerationSettings instance,
         * is because internally during init BiomeGenerationSettings creates a few Sets
         * based on the given Features, which wouldn't be updated by changing one field.
         * Now technically this could also be done by manually updating all of those fields,
         * however that is likely more fragile, so in this case this approach is cleaner.
         */
        ((BiomeAccessor) (Object) biome).setGenerationSettings(
            // Creates a new BiomeGenerationSettings instance with the specified features removed
            BiomeGenerationSettingsAccessor.createBiomeGenerationSettings(
                ((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
                biomeFeatures
            )
        );
    }
}
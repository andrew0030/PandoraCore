package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.BiomeGenerationSettingsAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AddFeaturesModifier} can be used to add {@link PlacedFeature} instances to {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:add_features",
 *   "biomes": "minecraft:plains",
 *   "features": "example_mod:test_bush_feature",
 *   "step": "vegetal_decoration"
 * }
 * }</pre>
 *
 * @param biomes   The {@link Biome} instances the {@code features} will be added to
 * @param features The {@link PlacedFeature} instances that will be added
 * @param step     The {@link GenerationStep.Decoration} step at which {@code features} will run
 */
public record AddFeaturesModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements Modifier {
    public static final Codec<AddFeaturesModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
        ).apply(instance, AddFeaturesModifier::new)
    );

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public Phase phase() {
        return Phase.ADD;
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomeHolders = this.biomes().stream().toList();
        for (Holder<Biome> holder : biomeHolders) {
            this.applyToBiome(holder.value());
        }
    }

    private void applyToBiome(Biome biome) {
        // Gets current biome generation settings and a copy of the internal list
        BiomeGenerationSettings settings = biome.getGenerationSettings();
        List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(settings.features());

        // Ensures the list is long enough to hold the modifier step index
        int index = this.step().ordinal();
        while (biomeFeatures.size() <= index) {
            biomeFeatures.add(HolderSet.direct()); // Empty entries for missing decoration steps
        }

        /*
         * Gets a list of the existing placed features, for the specified decoration step
         *
         * NOTE: Technically this could be a Set to de-dupe holders, however I'm not sure
         * if de-duping could break smt. So for now users need to ensure safety themselves
         */
        List<Holder<PlacedFeature>> existing = biomeFeatures.get(index).stream().toList();
        List<Holder<PlacedFeature>> toAdd = this.features().stream().toList();

        // If there are no addition no further logic needs to run
        if (toAdd.isEmpty()) return;

        // Merges the existing holders with the new ones, and then sets them as the holder set of the specified step
        List<Holder<PlacedFeature>> merged = new ArrayList<>(existing.size() + toAdd.size());
        merged.addAll(existing);
        merged.addAll(toAdd);
        biomeFeatures.set(index, HolderSet.direct(merged));

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
            // Creates a new BiomeGenerationSettings instance with the additional features
            BiomeGenerationSettingsAccessor.createBiomeGenerationSettings(
                ((BiomeGenerationSettingsAccessor) settings).getCarvers(),
                biomeFeatures
            )
        );
    }
}
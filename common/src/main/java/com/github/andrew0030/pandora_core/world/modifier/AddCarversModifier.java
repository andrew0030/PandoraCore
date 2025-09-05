package com.github.andrew0030.pandora_core.world.modifier;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.BiomeGenerationSettingsAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link AddCarversModifier} can be used to add {@link ConfiguredWorldCarver} instances to {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:add_carvers",
 *   "biomes": "minecraft:the_end",
 *   "carvers": "example_mod:example_end_carver",
 *   "step": "air" // This is optional, if omitted it defaults to "air"
 * }
 * }</pre>
 *
 * @param biomes  The {@link Biome} instances the {@code carvers} will be added to
 * @param carvers The {@link ConfiguredWorldCarver} instances that will be added
 * @param step    The {@link GenerationStep.Carving} step at which {@code carvers} will run
 */
public record AddCarversModifier(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, GenerationStep.Carving step) implements Modifier {
    public static final Codec<AddCarversModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddCarversModifier::biomes),
            ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(AddCarversModifier::carvers),
            GenerationStep.Carving.CODEC.optionalFieldOf("step", GenerationStep.Carving.AIR).forGetter(AddCarversModifier::step)
        ).apply(instance, AddCarversModifier::new)
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
        // Gets current biome generation settings and a copy of the internal map
        BiomeGenerationSettings settings = biome.getGenerationSettings();
        Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> biomeCarvers = new HashMap<>(((BiomeGenerationSettingsAccessor) settings).getCarvers());

        /*
         * Gets a list of the existing configured world carvers, for the specified carving step
         *
         * NOTE: Technically this could be a Set to de-dupe holders, however I'm not sure
         * if de-duping could break smt. So for now users need to ensure safety themselves
         */
        HolderSet<ConfiguredWorldCarver<?>> existingHolderSet = biomeCarvers.get(this.step());
        List<Holder<ConfiguredWorldCarver<?>>> existing = existingHolderSet != null ? existingHolderSet.stream().toList() : List.of();
        List<Holder<ConfiguredWorldCarver<?>>> toAdd = this.carvers().stream().toList();

        // If there are no addition no further logic needs to run
        if (toAdd.isEmpty()) return;

        // Merges the existing holders with the new ones, and then sets them as the holder set of the specified step
        List<Holder<ConfiguredWorldCarver<?>>> merged = new ArrayList<>(existing.size() + toAdd.size());
        merged.addAll(existing);
        merged.addAll(toAdd);
        biomeCarvers.put(this.step(), HolderSet.direct(merged));

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
            // Creates a new BiomeGenerationSettings instance with the additional carvers
            BiomeGenerationSettingsAccessor.createBiomeGenerationSettings(
                biomeCarvers,
                settings.features()
            )
        );
    }
}
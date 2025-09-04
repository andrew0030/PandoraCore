package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.MobSpawnSettingsAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@link AddSpawnCostsModifier} can be used to set the {@link MobSpawnSettings.MobSpawnCost} of {@link EntityType} instances in  {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:add_spawn_costs",
 *   "biomes": "example_mod:test_biome",
 *   "entity_types": [
 *     "example_mod:test_entity",
 *     "example_mod:other_test_entity"
 *   ],
 *   "spawn_cost": {
 *     "energy_budget": 0.7,
 *     "charge": 0.3
 *   }
 * }
 * }</pre>
 *
 * @param biomes      The {@link Biome} instances in which the {@code entityTypes} will get a {@code spawnCost}
 * @param entityTypes The {@link EntityType} instances that will get a {@code spawnCost}
 * @param spawnCost   The {@link MobSpawnSettings.MobSpawnCost} that will be applied to the {@code entityTypes}
 */
public record AddSpawnCostsModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes, MobSpawnSettings.MobSpawnCost spawnCost) implements Modifier {
    public static final Codec<AddSpawnCostsModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnCostsModifier::biomes),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(AddSpawnCostsModifier::entityTypes),
            MobSpawnSettings.MobSpawnCost.CODEC.fieldOf("spawn_cost").forGetter(AddSpawnCostsModifier::spawnCost)
        ).apply(instance, AddSpawnCostsModifier::new)
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
        // Gets current spawn settings and a mutable copy of the internal map
        MobSpawnSettings settings = biome.getMobSettings();
        Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts = new HashMap<>(((MobSpawnSettingsAccessor) settings).getMobSpawnCosts());

        // Builds a Set of entity types to quickly check if they are present
        Set<EntityType<?>> toAdd = this.entityTypes().stream().map(Holder::value).collect(Collectors.toCollection(HashSet::new));
        if (toAdd.isEmpty()) return; // If no entity types need to be added we skip further logic

        // A little smt to keep track of whether any changes happened
        boolean changed = false;

        // Iterates through the additions Set and adds all entries to the Map
        for (EntityType<?> entityType : toAdd) {
            MobSpawnSettings.MobSpawnCost oldSpawnCost = mobSpawnCosts.get(entityType);
            if (!Objects.equals(oldSpawnCost, this.spawnCost())) { // Skips replacing same value
                // NOTE: I used a 'put' here to allow for replacement of the vanilla values.
                // Users/Modders should use a 'pandora_core:none' modifier to overwrite other mods.
                mobSpawnCosts.put(entityType, this.spawnCost());
                changed = true;
            }
        }

        // If nothing changed, the mob spawn settings don't need to be updated
        if (!changed) return;

        /*
         * Updates the mob spawn settings of the Biome
         *
         * NOTE: The reason we are creating a new MobSpawnSettings instance, is because Forge
         * adds some fields to MobSpawnSettings, and initializes them using the existing fields.
         * Ergo if we try to modify just the field, Forge's field won't have the added entry.
         */
        ((BiomeAccessor) (Object) biome).setMobSettings(
            // Creates a new BiomeGenerationSettings instance with the additional features
            MobSpawnSettingsAccessor.createMobSpawnSettings(
                settings.getCreatureProbability(),
                ((MobSpawnSettingsAccessor) settings).getSpawners(),
                mobSpawnCosts
            )
        );
    }
}
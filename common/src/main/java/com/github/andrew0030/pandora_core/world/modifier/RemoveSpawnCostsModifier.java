package com.github.andrew0030.pandora_core.world.modifier;

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
 * The {@link RemoveSpawnCostsModifier} can be used to remove the {@link MobSpawnSettings.MobSpawnCost} of {@link EntityType} instances in  {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:remove_spawn_costs",
 *   "biomes": [
 *     "minecraft:soul_sand_valley",
 *     "minecraft:warped_forest"
 *   ],
 *   "entity_types": [
 *     "minecraft:enderman",
 *     "minecraft:skeleton"
 *   ]
 * }
 * }</pre>
 *
 * @param biomes      The {@link Biome} instances in which the {@code entityTypes} will get their {@code spawnCost} removed
 * @param entityTypes The {@link EntityType} instances that will get their {@code spawnCost} removed
 */
public record RemoveSpawnCostsModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements Modifier {
    public static final Codec<RemoveSpawnCostsModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveSpawnCostsModifier::biomes),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(RemoveSpawnCostsModifier::entityTypes)
        ).apply(instance, RemoveSpawnCostsModifier::new)
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
        // Gets current spawn settings and a mutable copy of the internal map
        MobSpawnSettings settings = biome.getMobSettings();
        Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts = new HashMap<>(((MobSpawnSettingsAccessor) settings).getMobSpawnCosts());

        // Builds a Set of entity types to quickly check if they are present
        Set<EntityType<?>> toRemove = this.entityTypes().stream().map(Holder::value).collect(Collectors.toCollection(HashSet::new));
        if (toRemove.isEmpty()) return; // If no entity types need to be removed we skip further logic

        // A little smt to keep track of whether any changes happened
        boolean changed = false;

        // Iterates through the removal set and removes matching entity types
        for (EntityType<?> entityType : toRemove) {
            if (mobSpawnCosts.remove(entityType) != null)
                changed = true;
        }

        // If nothing changed, the mob spawn settings don't need to be updated
        if (!changed) return;

        /*
         * Updates the mob spawn settings of the Biome
         *
         * NOTE: The reason we are creating a new MobSpawnSettings instance, is because Forge
         * adds some fields to MobSpawnSettings, and initializes them using the existing fields.
         * Ergo if we try to modify just the field, Forge's field will still have the removed entry.
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
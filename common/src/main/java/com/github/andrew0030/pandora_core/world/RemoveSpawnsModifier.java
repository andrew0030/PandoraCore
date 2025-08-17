package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.MobSpawnSettingsAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.*;
import java.util.stream.Collectors;

//TODO write javadocs
public record RemoveSpawnsModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements Modifier {
    public static final Codec<RemoveSpawnsModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveSpawnsModifier::biomes),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(RemoveSpawnsModifier::entityTypes)
        ).apply(instance, RemoveSpawnsModifier::new)
    );

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomeHolders = this.biomes().stream().toList();
        for (Holder<Biome> holder : biomeHolders) {
            this.removeFromBiome(holder.value());
        }
    }

    // TODO add phases to ensure removal modifiers run after addition modifiers

    private void removeFromBiome(Biome biome) {
        // Gets current spawn settings and a mutable copy of the internal map
        MobSpawnSettings spawnSettings = biome.getMobSettings();
        Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> biomeSpawners = new HashMap<>(((MobSpawnSettingsAccessor) spawnSettings).getSpawners());

        // Builds a Set of entity types to quickly check if they are present
        Set<EntityType<?>> toRemove = this.entityTypes().stream().map(Holder::value).collect(Collectors.toCollection(HashSet::new));
        if (toRemove.isEmpty()) return; // If no entity types need to be removed we skip further logic

        // Iterates through the categories present in the biome spawner map
        for (Map.Entry<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> entry : biomeSpawners.entrySet()) {
            MobCategory category = entry.getKey();
            WeightedRandomList<MobSpawnSettings.SpawnerData> existing = biomeSpawners.get(category);
            if (existing == null) continue; // If the category has no entries we skip further logic

            List<MobSpawnSettings.SpawnerData> base = existing.unwrap();

            // If none of the base entries are in the removal set, we skip further logic
            boolean anyRemoved = false;
            for (MobSpawnSettings.SpawnerData spawnerData : base) {
                if (toRemove.contains(spawnerData.type)) {
                    anyRemoved = true;
                    break;
                }
            }
            if (!anyRemoved) continue; // No changes for this category

            // If there are entries that need to be removed, we build a filtered list
            List<MobSpawnSettings.SpawnerData> filtered = new ArrayList<>(base.size());
            for (MobSpawnSettings.SpawnerData spawnerData : base) {
                if (!toRemove.contains(spawnerData.type))
                    filtered.add(spawnerData);
            }

            biomeSpawners.put(category, WeightedRandomList.create(filtered));
        }

        // Updates the spawn settings before updating the mob settings of the Biome
        ((MobSpawnSettingsAccessor) spawnSettings).setSpawners(biomeSpawners);
        ((BiomeAccessor) (Object) biome).setMobSettings(spawnSettings);
    }
}
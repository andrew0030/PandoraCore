package com.github.andrew0030.pandora_core.world.modifier;

import com.github.andrew0030.pandora_core.mixin.accessor.BiomeAccessor;
import com.github.andrew0030.pandora_core.mixin.accessor.MobSpawnSettingsAccessor;
import com.github.andrew0030.pandora_core.utils.PaCoCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.*;

/**
 * The {@link AddSpawnsModifier} can be used to add {@link MobSpawnSettings.SpawnerData} instances to {@link Biome} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:add_spawns",
 *   "biomes": "minecraft:plains",
 *   "spawners": {
 *     "type": "example_mod:test_entity",
 *     "weight": 100,
 *     "minCount": 1,
 *     "maxCount": 2
 *   }
 * }
 * }</pre>
 *
 * @param biomes   The {@link Biome} instances the {@code spawners} will be added to
 * @param spawners The {@link MobSpawnSettings.SpawnerData} instances that will be added
 */
public record AddSpawnsModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) implements Modifier {
    public static final Codec<AddSpawnsModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnsModifier::biomes),
            PaCoCodecUtils.singleOrList(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawners").forGetter(AddSpawnsModifier::spawners)
        ).apply(instance, AddSpawnsModifier::new)
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
        Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> biomeSpawners = new HashMap<>(((MobSpawnSettingsAccessor) settings).getSpawners());

        // Groups new spawners by category
        Map<MobCategory, List<MobSpawnSettings.SpawnerData>> toAdd = new EnumMap<>(MobCategory.class);
        for (MobSpawnSettings.SpawnerData entry : this.spawners()) {
            MobCategory category = entry.type.getCategory();
            toAdd.computeIfAbsent(category, cat -> new ArrayList<>()).add(entry);
        }

        // Bulk merges per category (one unwrap + one create per affected category)
        for (Map.Entry<MobCategory, List<MobSpawnSettings.SpawnerData>> entry : toAdd.entrySet()) {
            MobCategory category = entry.getKey();
            List<MobSpawnSettings.SpawnerData> additions = entry.getValue();
            WeightedRandomList<MobSpawnSettings.SpawnerData> existing = biomeSpawners.get(category);
            List<MobSpawnSettings.SpawnerData> merged = new ArrayList<>((existing != null ? existing.unwrap().size() : 0) + additions.size());

            if (existing != null) merged.addAll(existing.unwrap());
            merged.addAll(additions);

            biomeSpawners.put(category, WeightedRandomList.create(merged));
        }

        // Updates the spawn settings before updating the mob settings of the Biome
        ((MobSpawnSettingsAccessor) settings).setSpawners(biomeSpawners);
        ((BiomeAccessor) (Object) biome).setMobSettings(settings);
    }
}
package com.github.andrew0030.pandora_core.world.modifier;

import com.github.andrew0030.pandora_core.mixin.accessor.StructureSetAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@link RemoveStructureSetStructuresModifier} can be used to remove {@link Structure} instances from {@link StructureSet} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:remove_structure_set_structures",
 *   "structure_sets": [
 *     "minecraft:villages"
 *   ],
 *   "structures": [
 *     "minecraft:village_plains"
 *   ]
 * }
 * }</pre>
 *
 * @param structureSets The {@link StructureSet} instances from which {@code structures} will be removed
 * @param structures    The {@link Structure} instances that will be removed
 */
public record RemoveStructureSetStructuresModifier(HolderSet<StructureSet> structureSets, HolderSet<Structure> structures) implements Modifier {
    public static final Codec<RemoveStructureSetStructuresModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).fieldOf("structure_sets").forGetter(RemoveStructureSetStructuresModifier::structureSets),
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(RemoveStructureSetStructuresModifier::structures)
        ).apply(instance, RemoveStructureSetStructuresModifier::new)
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
        List<Holder<StructureSet>> structureSetHolders = this.structureSets().stream().toList();
        for (Holder<StructureSet> holder : structureSetHolders) {
            this.removeFromStructureSet(holder.value());
        }
    }

    private void removeFromStructureSet(StructureSet structureSet) {
        // Builds a Set of structures to quickly check if they are present
        Set<Structure> toRemove = this.structures().stream().map(Holder::value).collect(Collectors.toCollection(HashSet::new));
        if (toRemove.isEmpty()) return; // If no structures need to be removed we skip further logic

        // If there are entries that need to be removed, we build a filtered list
        List<StructureSet.StructureSelectionEntry> filtered = structureSet.structures().stream().filter(entry -> !toRemove.contains(entry.structure().value())).toList();

        // If nothing changed, the mob spawn settings don't need to be updated
        if (filtered.size() == structureSet.structures().size()) return;

        // Updates the structures list of the structure set
        ((StructureSetAccessor) (Object) structureSet).setStructures(filtered);
    }
}
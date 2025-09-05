package com.github.andrew0030.pandora_core.world.modifier;

import com.github.andrew0030.pandora_core.mixin.accessor.StructureSetAccessor;
import com.github.andrew0030.pandora_core.utils.PaCoCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AddStructureSetStructuresModifier} can be used to add {@link StructureSet.StructureSelectionEntry} instances to {@link StructureSet} instances.
 * <p>Usage example:</p>
 * <pre>{@code
 * {
 *   "type": "pandora_core:add_structure_set_structures",
 *   "structure_sets": "minecraft:villages",
 *   "structures": [
 *     {
 *       "structure": "example_mod:example_village",
 *       "weight": 1
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @param structureSets The {@link StructureSet} instances the {@code structures} will be added to
 * @param structures    The {@link StructureSet.StructureSelectionEntry} instances that will be added
 */
public record AddStructureSetStructuresModifier(HolderSet<StructureSet> structureSets, List<StructureSet.StructureSelectionEntry> structures) implements Modifier {
    public static final Codec<AddStructureSetStructuresModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).fieldOf("structure_sets").forGetter(AddStructureSetStructuresModifier::structureSets),
            PaCoCodecUtils.singleOrList(StructureSet.StructureSelectionEntry.CODEC).fieldOf("structures").forGetter(AddStructureSetStructuresModifier::structures)
        ).apply(instance, AddStructureSetStructuresModifier::new)
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
        List<Holder<StructureSet>> structureSetHolders = this.structureSets().stream().toList();
        for (Holder<StructureSet> holder : structureSetHolders) {
            this.addToStructureSet(holder.value());
        }
    }

    private void addToStructureSet(StructureSet structureSet) {
        List<StructureSet.StructureSelectionEntry> existing = structureSet.structures();
        List<StructureSet.StructureSelectionEntry> toAdd = this.structures();
        // If no structures need to be added we skip further logic
        if (toAdd.isEmpty()) return;

        // Merges the existing structures with the new ones
        List<StructureSet.StructureSelectionEntry> merged = new ArrayList<>(existing.size() + toAdd.size());
        merged.addAll(existing);
        merged.addAll(toAdd);

        // Updates the structures list of the structure set
        ((StructureSetAccessor) (Object) structureSet).setStructures(merged);
    }
}
package com.github.andrew0030.pandora_core.world;

import com.github.andrew0030.pandora_core.mixin.accessor.StructureTemplatePoolAccessor;
import com.github.andrew0030.pandora_core.utils.PaCoCodecUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;

public record AddTemplatePoolElementsModifier(HolderSet<StructureTemplatePool> templatePools, List<Pair<StructurePoolElement, Integer>> elements) implements Modifier {
    // A Codec representing a weighted structure pool element
    public static final Codec<Pair<StructurePoolElement, Integer>> WEIGHTED_POOL_ELEMENT_CODEC = Codec.mapPair(
        StructurePoolElement.CODEC.fieldOf("element"),
        Codec.intRange(1, 1000).fieldOf("weight")
    ).codec();
    // The Modifier's Codec
    public static final Codec<AddTemplatePoolElementsModifier> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            RegistryCodecs.homogeneousList(Registries.TEMPLATE_POOL).fieldOf("template_pools").forGetter(AddTemplatePoolElementsModifier::templatePools),
            PaCoCodecUtils.singleOrList(WEIGHTED_POOL_ELEMENT_CODEC).fieldOf("elements").forGetter(AddTemplatePoolElementsModifier::elements)
        ).apply(instance, AddTemplatePoolElementsModifier::new)
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
        List<Holder<StructureTemplatePool>> templatePoolHolders = this.templatePools().stream().toList();
        for (Holder<StructureTemplatePool> holder : templatePoolHolders) {
            this.applyToStructure(holder.value());
        }
    }

    private void applyToStructure(StructureTemplatePool templatePool) {
        // Gets the structure template pool accessor, to avoid continuous casts
        StructureTemplatePoolAccessor accessor = ((StructureTemplatePoolAccessor) templatePool);

        // If there are no addition no further logic needs to run
        if (this.elements().isEmpty()) return;

        // Gets the existing rawTemplates list, and adds all elements this modifier is adding
        List<Pair<StructurePoolElement, Integer>> mutableRawTemplates = new ArrayList<>(accessor.getRawTemplates());
        mutableRawTemplates.addAll(this.elements());
        accessor.setRawTemplates(mutableRawTemplates);

        /*
         * Gets the existing templates list, and adds all elements this modifier is adding.
         * Elements are added n times, (where n is the element weight). This is essentially also what minecraft does.
         */
        ObjectArrayList<StructurePoolElement> mutableTemplates = new ObjectArrayList<>(accessor.getTemplates());
        for (Pair<StructurePoolElement, Integer> pair : this.elements()) {
            StructurePoolElement poolElement = pair.getFirst();
            int weight = pair.getSecond();
            for (int i = 0; i < weight; i++) {
                mutableTemplates.add(poolElement);
            }
        }
        accessor.setTemplates(mutableTemplates);
    }
}
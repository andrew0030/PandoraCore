package com.github.andrew0030.pandora_core.mixin.accessor;

import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureSet.class)
public interface StructureSetAccessor {
    @Accessor("structures")
    @Mutable
    void setStructures(List<StructureSet.StructureSelectionEntry> structures);
}
package com.github.andrew0030.pandora_core.registry.internal;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.registry.PaCoRegistry;
import com.github.andrew0030.pandora_core.world.structure.pools.ExpandedStructurePoolElement;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import java.util.function.Supplier;

public class PaCoStructurePoolElementTypes {
    public static final PaCoRegistry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_TYPES = new PaCoRegistry<>(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, PandoraCore.MOD_ID);

    public static final Supplier<StructurePoolElementType<ExpandedStructurePoolElement>> EXPANDED = STRUCTURE_POOL_ELEMENT_TYPES.add("expanded", () -> codec(ExpandedStructurePoolElement.CODEC));

    /**
     * We need this little helper because some IDEs cannot resolve the types correctly.
     * So to prevent the compiler from yelling at us in those cases, we explicitly state the return types.
     */
    private static <P extends StructurePoolElement> StructurePoolElementType<P> codec(Codec<P> codec) {
        return () -> codec;
    }
}
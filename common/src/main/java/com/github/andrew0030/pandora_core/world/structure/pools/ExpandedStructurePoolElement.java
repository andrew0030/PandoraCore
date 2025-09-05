package com.github.andrew0030.pandora_core.world.structure.pools;

import com.github.andrew0030.pandora_core.registry.internal.PaCoStructurePoolElementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//TODO: make jigsaw generation use this pool element, or well more specifically apply conditional placement like "min_count" or "max_count"
public class ExpandedStructurePoolElement extends StructurePoolElement {
    // Codec
    public static final Codec<ExpandedStructurePoolElement> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            StructurePoolElement.CODEC.fieldOf("expanded_element").forGetter(ExpandedStructurePoolElement::structurePoolElement)
        ).apply(instance, ExpandedStructurePoolElement::new)
    );
    // Internal fields
    public final StructurePoolElement structurePoolElement;

    public ExpandedStructurePoolElement(StructurePoolElement structurePoolElement) {
        super(structurePoolElement.getProjection());
        this.structurePoolElement = structurePoolElement;
    }

    @Override
    public @NotNull Vec3i getSize(@NotNull StructureTemplateManager manager, @NotNull Rotation rotation) {
        return this.structurePoolElement.getSize(manager, rotation);
    }

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(@NotNull StructureTemplateManager manager, @NotNull BlockPos pos, @NotNull Rotation rotation, @NotNull RandomSource random) {
        return this.structurePoolElement.getShuffledJigsawBlocks(manager, pos, rotation, random);
    }

    @Override
    public @NotNull BoundingBox getBoundingBox(@NotNull StructureTemplateManager manager, @NotNull BlockPos pos, @NotNull Rotation rotation) {
        return this.structurePoolElement.getBoundingBox(manager, pos, rotation);
    }

    @Override
    public boolean place(
            @NotNull StructureTemplateManager structureTemplateManager,
            @NotNull WorldGenLevel level,
            @NotNull StructureManager structureManager,
            @NotNull ChunkGenerator generator,
            @NotNull BlockPos offset,
            @NotNull BlockPos pos,
            @NotNull Rotation rotation,
            @NotNull BoundingBox box,
            @NotNull RandomSource random,
            boolean keepJigsaws
    ) {
        return this.structurePoolElement.place(structureTemplateManager, level, structureManager, generator, offset, pos, rotation, box, random, keepJigsaws);
    }

    @Override
    public @NotNull StructurePoolElementType<?> getType() {
        return PaCoStructurePoolElementTypes.EXPANDED.get();
    }

    private StructurePoolElement structurePoolElement() {
        return this.structurePoolElement;
    }
}
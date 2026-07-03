package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChunkRenderDispatcher.CompiledChunk.class)
public class CompiledChunkMixin implements InstancingResults {
    @Unique List<BlockEntity> pandoraCore$instancableBEs = new ArrayList<>();

    @Override
    public void addInstancer(BlockEntity be) {
        pandoraCore$instancableBEs.add(be);
    }

    @Override
    public List<BlockEntity> getAll() {
        return pandoraCore$instancableBEs;
    }

    @Override
    public void addAll(List<BlockEntity> all) {
        pandoraCore$instancableBEs.addAll(all);
    }
}
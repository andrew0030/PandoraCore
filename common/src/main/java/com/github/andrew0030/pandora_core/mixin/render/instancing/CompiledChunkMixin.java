package com.github.andrew0030.pandora_core.mixin.render.instancing;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChunkRenderDispatcher.CompiledChunk.class)
public class CompiledChunkMixin implements InstancingResults {
    List<BlockEntity> instancableBEs = new ArrayList<>();

    @Override
    public void addInstancer(BlockEntity be) {
        instancableBEs.add(be);
    }

    @Override
    public List<BlockEntity> getAll() {
        return instancableBEs;
    }

    @Override
    public void addAll(List<BlockEntity> all) {
        instancableBEs.addAll(all);
    }
}

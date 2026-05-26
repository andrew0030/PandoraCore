package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.vanilla;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults.class)
public class CompilationResultMixin implements InstancingResults {
    List<BlockEntity> instancableBlockEntities = new ArrayList<>();

    @Override
    public void addInstancer(BlockEntity be) {
        instancableBlockEntities.add(be);
    }

    @Override
    public List<BlockEntity> getAll() {
        return instancableBlockEntities;
    }

    @Override
    public void addAll(List<BlockEntity> all) {
        instancableBlockEntities.addAll(all);
    }
}

package com.github.andrew0030.pandora_core.client.render.renderers.backend;

import com.github.andrew0030.pandora_core.client.render.renderers.instancing.InstancedBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Idk how you would end up using this, lol
 */
@ApiStatus.Internal
@Deprecated(forRemoval = false)
public interface InstancingResults {
    void addInstancer(BlockEntity be);

    List<BlockEntity> getAll();

    void addAll(List<BlockEntity> all);
}

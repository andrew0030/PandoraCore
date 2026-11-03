package com.github.andrew0030.pandora_core.modules.instancer.renderers.backend;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Idk how you would end up using this, lol
 */
@ApiStatus.Internal
@Deprecated(forRemoval = false)
public interface InstancingResults {
    void pandoraCore$addInstancer(BlockEntity be);
    List<BlockEntity> pandoraCore$getAll();
    void pandoraCore$addAll(List<BlockEntity> all);
}
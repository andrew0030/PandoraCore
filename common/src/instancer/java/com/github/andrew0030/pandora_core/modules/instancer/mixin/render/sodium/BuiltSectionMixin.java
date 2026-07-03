package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.sodium;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.InstancingResults;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(BuiltSectionInfo.class)
public class BuiltSectionMixin implements InstancingResults {
//    public BlockEntity @Nullable [] instancableBlockEntities;
    @Unique public List<BlockEntity> pandoraCore$instancableBlockEntities = new ArrayList<>();

    @Override
    public void addInstancer(BlockEntity be) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public List<BlockEntity> getAll() {
        return pandoraCore$instancableBlockEntities;
    }

    @Override
    public void addAll(List<BlockEntity> all) {
        this.pandoraCore$instancableBlockEntities = all;
    }
}
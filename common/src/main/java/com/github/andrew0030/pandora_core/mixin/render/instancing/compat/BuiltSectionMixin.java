package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(BuiltSectionInfo.class)
public class BuiltSectionMixin implements InstancingResults {
//    public BlockEntity @Nullable [] instancableBlockEntities;
    public List<BlockEntity> instancableBlockEntities = new ArrayList<>();

    @Override
    public void addInstancer(BlockEntity be) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public List<BlockEntity> getAll() {
        return instancableBlockEntities;
    }

    @Override
    public void addAll(List<BlockEntity> all) {
        this.instancableBlockEntities = all;
    }
}

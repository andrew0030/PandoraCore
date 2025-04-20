package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import com.github.andrew0030.pandora_core.client.render.renderers.backend.sodium.RenderSectionAttachments;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RenderSection.class, remap = false)
public class RenderSectionMixin implements InstancingResults, RenderSectionAttachments {
    @Shadow private int flags;
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

    @Override
    public boolean hasInstancedBlockEntities() {
        return !instancableBlockEntities.isEmpty();
    }

    @Inject(at = @At("RETURN"), method = "setRenderState")
    public void postSetRenderState(BuiltSectionInfo info, CallbackInfo ci) {
        instancableBlockEntities = ((InstancingResults) info).getAll();
        if (!instancableBlockEntities.isEmpty()) {
            flags |= -1;
        }
    }
}

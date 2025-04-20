package com.github.andrew0030.pandora_core.mixin.render.instancing.compat;

import com.github.andrew0030.pandora_core.client.render.renderers.backend.InstancingResults;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BuiltSectionInfo.Builder.class, remap = false)
public class SectionBuilderMixin implements InstancingResults {
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

    @Inject(at = @At("RETURN"), method = "build")
    public void postBuild(CallbackInfoReturnable<BuiltSectionInfo> cir) {
        ((InstancingResults) cir.getReturnValue()).addAll(instancableBEs);
    }
}

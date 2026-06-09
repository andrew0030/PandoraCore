package com.github.andrew0030.pandora_core.modules.instancer.mixin.render.attach;

import com.github.andrew0030.pandora_core.modules.instancer.renderers.backend.BlockEntityTypeAttachments;
import com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing.InstancedBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements BlockEntityTypeAttachments {
    @Unique
    InstancedBlockEntityRenderer<?> pandoraCore$renderer;

    @Override
    public void pandoraCore$setInstancedRenderer(InstancedBlockEntityRenderer<?> renderer) {
        this.pandoraCore$renderer = renderer;
    }

    @Override
    public InstancedBlockEntityRenderer<?> pandoraCore$getInstancedRenderer() {
        return pandoraCore$renderer;
    }
}

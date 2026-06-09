package com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing;

import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.PacoInstancingLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class InstancedBlockEntityRenderer<T extends BlockEntity> extends InstanceRenderer<PacoInstancingLevel, T, BlockPos> {
    public InstancedBlockEntityRenderer(InstanceFormat format) {
        super(format);
    }

    @Override
    public boolean shouldRender(T object, Vec3 pCameraPos) {
        return Vec3.atCenterOf(object.getBlockPos()).closerThan(pCameraPos, this.getViewDistance());
    }
}

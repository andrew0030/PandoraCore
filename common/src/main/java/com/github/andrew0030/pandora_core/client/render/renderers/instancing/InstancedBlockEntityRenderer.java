package com.github.andrew0030.pandora_core.client.render.renderers.instancing;

import com.github.andrew0030.pandora_core.client.render.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.client.render.instancing.InstanceFormat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class InstancedBlockEntityRenderer<T extends BlockEntity> extends InstanceRenderer<T, BlockPos> {
    public InstancedBlockEntityRenderer(InstanceFormat format, CollectiveVBO vbo) {
        super(format, vbo);
    }

    @Override
    public boolean shouldRender(T object, Vec3 pCameraPos) {
        return Vec3.atCenterOf(object.getBlockPos()).closerThan(pCameraPos, this.getViewDistance());
    }
}

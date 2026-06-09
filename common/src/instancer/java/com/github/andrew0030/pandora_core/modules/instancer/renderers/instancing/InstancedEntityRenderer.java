package com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class InstancedEntityRenderer<T extends Entity> extends InstanceRenderer<T, Vec3> {
    public InstancedEntityRenderer(InstanceFormat format) {
        super(format);
    }

    @Override
    public boolean shouldRender(T object, Vec3 pCameraPos) {
	    return object.shouldRender(pCameraPos.x, pCameraPos.y, pCameraPos.z);
    }
}

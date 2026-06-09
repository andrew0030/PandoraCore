package com.github.andrew0030.pandora_core.modules.instancer.renderers.instancing;

import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.collective.CollectiveVBO;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.InstanceFormat;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin.ItemDrawData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchData;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.BatchKey;
import com.github.andrew0030.pandora_core.modules.instancer.instancing.engine.InstancingEnvironment;
import com.github.andrew0030.pandora_core.test.PaCoRenderTypes;
import com.github.andrew0030.pandora_core.test.TemplateShaderTest;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class InstancedItemRenderer extends InstanceRenderer<InstancingEnvironment, ItemStack, ItemDrawData> {
	public InstancedItemRenderer(InstanceFormat format) {
		super(format);
	}
	
	@Override
    public final boolean shouldRender(ItemStack object, Vec3 pCameraPos) {
	    return true;
    }
}

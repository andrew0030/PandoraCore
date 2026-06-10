package com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;

public class ItemDrawData {
	PoseStack matr;
	int lightMap;
	ItemDisplayContext displayContext;
	
	public PoseStack getPoseStack() {
		return matr;
	}
	
	public int getLightmap() {
		return lightMap;
	}
	
	public ItemDisplayContext getDisplayContext() {
		return displayContext;
	}
	
	public void setMatr(PoseStack matr) {
		this.matr = matr;
	}
	
	public void setLightMap(int lightMap) {
		this.lightMap = lightMap;
	}
	
	public void setDisplayContext(ItemDisplayContext displayContext) {
		this.displayContext = displayContext;
	}
}

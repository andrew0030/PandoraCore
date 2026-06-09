package com.github.andrew0030.pandora_core.modules.instancer.instancing.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

public class ItemDrawData {
	PoseStack matr;
	int lightMap;
	
	public PoseStack getPoseStack() {
		return matr;
	}
	
	public int getLightmap() {
		return lightMap;
	}
}

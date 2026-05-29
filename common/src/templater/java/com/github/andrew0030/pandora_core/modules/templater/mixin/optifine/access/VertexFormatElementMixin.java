package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access;

import com.github.andrew0030.pandora_core.modules.templater.itf.IVertexFormatElementAccess;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexFormatElement.class)
public class VertexFormatElementMixin implements IVertexFormatElementAccess {
	@Shadow
	@Final
	private int index;
	
	@Override
	public int pandoraCore$getIndex() {
		return index;
	}
}

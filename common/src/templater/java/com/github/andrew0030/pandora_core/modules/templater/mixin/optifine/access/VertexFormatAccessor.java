package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.access;

import com.github.andrew0030.pandora_core.modules.templater.itf.IVertexFormatAccess;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexFormat.class)
public class VertexFormatAccessor implements IVertexFormatAccess {
	@Shadow
	@Final
	private ImmutableMap<String, VertexFormatElement> elementMapping;
	
	@Override
	public ImmutableMap<String, VertexFormatElement> pandoraCore$getMappings() {
		return elementMapping;
	}
}

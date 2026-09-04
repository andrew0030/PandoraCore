package com.github.andrew0030.pandora_core.mixin.render;

import com.github.andrew0030.pandora_core.mixin_interfaces.render.IPaCoAccessibleBufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public class BufferBuilderAccessorMixin implements IPaCoAccessibleBufferBuilder {
	@Shadow
	private VertexFormat.Mode mode;
	
	@Shadow
	private int renderedBufferPointer;
	
	@Shadow
	private int vertices;
	
	@Shadow
	private VertexFormat format;
	
	public VertexFormat.Mode pandoraCore$getDrawMode() {
		return mode;
	}
	
	@Override
	public VertexFormat pandoraCore$getFormat() {
		return format;
	}
	
	@Override
	public int pandoraCore$getRenderedBufferPointer() {
		return renderedBufferPointer;
	}
	
	@Override
	public int pandoraCore$getVertexCount() {
		return vertices;
	}
}

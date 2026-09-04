package com.github.andrew0030.pandora_core.mixin_interfaces.render;

import com.mojang.blaze3d.vertex.VertexFormat;

public interface IPaCoAccessibleBufferBuilder {
	VertexFormat.Mode pandoraCore$getDrawMode();
	VertexFormat pandoraCore$getFormat();
	int pandoraCore$getRenderedBufferPointer();
	int pandoraCore$getVertexCount();
}

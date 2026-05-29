package com.github.andrew0030.pandora_core.modules.templater.mixin.optifine.dmp;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {
		"com.mojang.blaze3d.vertex.DefaultVertexFormat",
		"com.mojang.blaze3d.vertex.VertexFormat",
		"com.mojang.blaze3d.vertex.VertexFormatElement"
})
public class DoDump {
}

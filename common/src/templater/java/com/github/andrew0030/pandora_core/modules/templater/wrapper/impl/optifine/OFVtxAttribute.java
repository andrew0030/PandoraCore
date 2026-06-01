package com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine;

import net.optifine.shaders.Shaders;

import java.util.function.Supplier;

public enum OFVtxAttribute {
	ENTITY(() -> Shaders.useEntityAttrib, () -> Shaders.progUseEntityAttrib, () -> Shaders.entityAttrib, "mc_Entity"),
	MID_TEX_COORD(() -> Shaders.useMidTexCoordAttrib, () -> Shaders.progUseMidTexCoordAttrib, () -> Shaders.midTexCoordAttrib, "mc_midTexCoord"),
	TANGENT(() -> Shaders.useTangentAttrib, () -> Shaders.progUseTangentAttrib, () -> Shaders.tangentAttrib, "at_tangent"),
	VELOCITY(() -> Shaders.useVelocityAttrib, () -> Shaders.progUseVelocityAttrib, () -> Shaders.velocityAttrib, "at_velocity"),
	MID_BLOCK(() -> Shaders.useMidBlockAttrib, () -> Shaders.progUseMidBlockAttrib, () -> Shaders.midBlockAttrib, "at_midBlock")
	;
	
	public final Supplier<Boolean> useAttrib;
	public final Supplier<Boolean> progUseAttrib;
	public final Supplier<Integer> id;
	public final String ofName;
	
	OFVtxAttribute(Supplier<Boolean> useAttrib, Supplier<Boolean> progUseAttrib, Supplier<Integer> id, String ofName) {
		this.useAttrib = useAttrib;
		this.progUseAttrib = progUseAttrib;
		this.id = id;
		this.ofName = ofName;
	}
}

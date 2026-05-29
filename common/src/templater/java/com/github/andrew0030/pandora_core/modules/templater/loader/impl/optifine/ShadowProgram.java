package com.github.andrew0030.pandora_core.modules.templater.loader.impl.optifine;

public enum ShadowProgram {
	// this is going to be needed...
	NONE("none", null),
	SHADOW("shadow", NONE),
	SHADOW_SOLID("shadow_solid", SHADOW),
	SHADOW_CUTOUT("shadow_cutout", SHADOW);
	
	public final String name;
	public final ShadowProgram fallback;
	
	ShadowProgram(String name, ShadowProgram fallback) {
		this.name = name;
		this.fallback = fallback;
	}
	
	private static final ShadowProgram[] TYPES = ShadowProgram.values();
	
	public static ShadowProgram getType(String template) {
		String extName = template;
		for (ShadowProgram type : TYPES) {
			if (type.name.equals(extName)) {
				return type;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
}

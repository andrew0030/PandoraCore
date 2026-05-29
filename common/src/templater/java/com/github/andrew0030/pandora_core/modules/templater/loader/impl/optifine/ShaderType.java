package com.github.andrew0030.pandora_core.modules.templater.loader.impl.optifine;

public enum ShaderType {
	NONE("none", null),
	BASIC("gbuffers_basic", NONE),
	LINE("gbuffers_line", BASIC),
	TEXTURED("gbuffers_textured", BASIC),
	TEXTURED_LIT("gbuffers_textured_lit", TEXTURED),
	SKY_BASIC("gbuffers_skybasic", BASIC),
	SKY_TEXTURED("gbuffers_skytextured", TEXTURED),
	CLOUDS("gbuffers_clouds", TEXTURED),
	TERRAIN("gbuffers_terrain", TEXTURED_LIT),
	TERRAIN_SOLID("gbuffers_terrain_solid", TERRAIN),
	TERRAIN_CUTOUT_MIP("gbuffers_terrain_cutout_mip", TERRAIN),
	TERRAIN_CUTOUT("gbuffers_terrain_cutout", TERRAIN),
	DAMAGED_BLOCK("gbuffers_damagedblock", TERRAIN),
	BLOCK("gbuffers_block", TERRAIN),
	BEACON_BEAM("gbuffers_beaconbeam", TEXTURED),
	ITEM("gbuffers_item", TEXTURED_LIT),
	ENTITIES("gbuffers_entities", TEXTURED_LIT),
	ENTITIES_GLOWING("gbuffers_entities_glowing", ENTITIES),
	ARMOR_GLINT("gbuffers_armor_glint", TEXTURED),
	SPIDER_EYES("gbuffers_spidereyes", TEXTURED),
	HAND("gbuffers_hand", TEXTURED_LIT),
	WEATHER("gbuffers_weather", TEXTURED_LIT),
	WATER("gbuffers_water", TERRAIN),
	HAND_WATER("gbuffers_hand_water", HAND);
	
	public final String name;
	public final ShaderType fallback;
	
	ShaderType(String name, ShaderType fallback) {
		this.name = name;
		this.fallback = fallback;
	}
	
	private static final ShaderType[] TYPES = ShaderType.values();
	
	public static ShaderType getType(String template) {
		String extName = "gbuffers_" + template;
		for (ShaderType type : TYPES) {
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

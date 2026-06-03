package com.github.andrew0030.pandora_core.utils.debug;

public class PaCoProperties {
	public static final boolean shaderDump = boolProp("paco.debug.shader_dump", false);
	public static final boolean bytecodeDump = boolProp("paco.debug.class_dump", false);
	public static final boolean zinkPatch = boolProp("paco.patches.zink_windows", false);
	public static final boolean renderdoc = boolProp("paco.use.renderdoc", false);
	
	public static boolean boolProp(String name, boolean defaultValue) {
		boolean value = _boolProp(name, defaultValue);
		System.out.println(name + " -> " + value + "/" + defaultValue);
		return value;
	}
	
	public static boolean _boolProp(String name, boolean defaultValue) {
		String txt = System.getProperty(name);
		if (txt == null) return defaultValue;
		return switch (txt) {
			case "true", "1" -> true;
			case "false", "0" -> false;
			default -> defaultValue;
		};
	}
}

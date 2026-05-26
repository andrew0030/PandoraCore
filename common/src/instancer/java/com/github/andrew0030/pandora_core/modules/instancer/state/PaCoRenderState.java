package com.github.andrew0030.pandora_core.modules.instancer.state;

import com.github.andrew0030.pandora_core.modules.templater.TemplateManager;
import com.github.andrew0030.pandora_core.modules.templater.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.modules.templater.loader.ShaderCapability;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.loader.impl.VanillaTemplateLoader;
import com.github.andrew0030.pandora_core.utils.shader_checker.ShaderChecker;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;

public class PaCoRenderState {
	private static ShaderCapability[] PREFERRED_CAPABILITIES;
	private static TemplateLoader PREFERRED_LOADER;
	
	public static void setupUI() {
		PREFERRED_CAPABILITIES = new ShaderCapability[]{
				ShaderCapabilities.UI_DRAW
		};
		PREFERRED_LOADER = VanillaTemplateLoader.getInstance();
	}
	
	public static void setupWorld() {
		if (ShaderChecker.isShaderActive()) {
			PREFERRED_CAPABILITIES = new ShaderCapability[]{
					ShaderCapabilities.WORLD_DRAW,
					ShaderCapabilities.SHADOW_DRAW
			};
		} else {
			PREFERRED_CAPABILITIES = new ShaderCapability[]{
					ShaderCapabilities.WORLD_DRAW
			};
		}
		PREFERRED_LOADER = TemplateManager.chooseLoader(PREFERRED_CAPABILITIES);
	}
	
	public static void resetInstancerState() {
		PREFERRED_CAPABILITIES = new ShaderCapability[0];
		PREFERRED_LOADER = null;
	}
	
	public static ShaderCapability[] getPreferredCapabilities() {
		return PREFERRED_CAPABILITIES;
	}
	
	public static TemplateLoader getPreferredLoader() {
		return PREFERRED_LOADER;
	}
	
	public static void setPreferredCapabilities(ShaderCapability[] preferredCapabilities) {
		PREFERRED_CAPABILITIES = preferredCapabilities;
	}
	
	public static void setPreferredLoader(TemplateLoader preferredLoader) {
		PREFERRED_LOADER = preferredLoader;
	}
	
	public static boolean isGUI() {
		if (PREFERRED_CAPABILITIES.length == 1 && PREFERRED_CAPABILITIES[0] == ShaderCapabilities.UI_DRAW) {
			return true;
		}
		return false;
	}
}

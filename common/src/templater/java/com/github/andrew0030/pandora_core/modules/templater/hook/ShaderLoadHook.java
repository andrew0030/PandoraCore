package com.github.andrew0030.pandora_core.modules.templater.hook;

import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ShaderLoadHook {
	public static List<String> preSource(TemplateLoader loader, List<String> source, ResourceLocation resourceLocation) {
		return source;
	}
	
	public static List<String> postSource(TemplateLoader loader, List<String> source, ResourceLocation resourceLocation) {
		return source;
	}
}

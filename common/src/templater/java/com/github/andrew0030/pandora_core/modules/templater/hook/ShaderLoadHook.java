package com.github.andrew0030.pandora_core.modules.templater.hook;

import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.utils.debug.PaCoProperties;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShaderLoadHook {
	private static boolean enableDump = PaCoProperties.baseShaderDump;
	
	public static List<String> preSource(TemplateLoader loader, List<String> source, ResourceLocation resourceLocation) {
		if (enableDump) {
			try {
				StringBuilder builder = new StringBuilder();
				for (String s : source) {
					builder.append(s).append("\n");
				}
				
				File fl = new File("paco_shader_dump/base/pre/" + loader.name() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath().replace("/", "/") + ".class");
				fl.getParentFile().mkdirs();
				FileOutputStream outputStream = new FileOutputStream(fl);
				outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return source;
	}
	
	public static List<String> postSource(TemplateLoader loader, List<String> source, ResourceLocation resourceLocation) {
		if (enableDump) {
			try {
				StringBuilder builder = new StringBuilder();
				for (String s : source) {
					builder.append(s).append("\n");
				}
				
				File fl = new File("paco_shader_dump/base/post/" + loader.name() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath().replace("/", "/") + ".class");
				fl.getParentFile().mkdirs();
				FileOutputStream outputStream = new FileOutputStream(fl);
				outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return source;
	}
}

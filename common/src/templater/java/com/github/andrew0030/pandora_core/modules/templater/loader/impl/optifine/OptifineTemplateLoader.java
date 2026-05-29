package com.github.andrew0030.pandora_core.modules.templater.loader.impl.optifine;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.modules.templater.NameMapper;
import com.github.andrew0030.pandora_core.modules.templater.TemplateManager;
import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.IrisTemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OptifineTemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentType;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.optifine.shaders.Program;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class OptifineTemplateLoader extends TemplateLoader implements VariableMapper {
	private static String ACTIVE = null;
	private static List<String> SOURCE = new ArrayList<>();
	
	private static Map<String, List<String>> sources = new HashMap<>();
	
	private final TransformationProcessor processor = new DefaultTransformationProcessor();
	
	private static OptifineTemplateLoader INSTANCE;
	
	public static OptifineTemplateLoader getInstance() {
		return INSTANCE;
	}
	
	public OptifineTemplateLoader() {
		super(ShaderCapabilities.CAPABILITIES_WORLD_SHADOW);
		if (INSTANCE != null)
			throw new RuntimeException("Cannot create two vanilla template loaders.");
		INSTANCE = this;
	}
	
	public static void activeFile(String file) {
		ACTIVE = file;
		SOURCE = new ArrayList<>();
	}
	
	public static void shaderSource(List<String> $$1) {
		System.out.println("SOURCE: " + ACTIVE);
		SOURCE.addAll($$1);
	}
	
	static boolean forceLoad = false;
	
	public static void link() {
		System.out.println("LINK: " + ACTIVE);
		if (ACTIVE != null)
			sources.put(ACTIVE, new ReadOnlyList<>(SOURCE));
	}
	
	public static void cancel() {
		ACTIVE = null;
	}
	
	private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Iris");
	
	private static final Map<String, Program> instances = new HashMap<>();
	private static final List<String> deferredLoad = new ArrayList<>();
	
	public static void bindShader(String $$1, Program shaderInstance) {
		System.out.println("BIND: " + $$1);
		instances.put($$1, shaderInstance);
		if (!forceLoad)
//            TemplateManager.reloadTemplate(INSTANCE, $$1);
			deferredLoad.add($$1);
	}
	
	public static void unbindShader(String pandoraCore$cacheName, Program instance) {
		instances.remove(pandoraCore$cacheName, instance);
	}
	
	private void getVertex(String template, boolean complete, AttachmentSpecifier[] specifiers) {
		List<String> res = sources.get(template + ".vsh");
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[0] = new AttachmentSpecifier(
				AttachmentType.VERTEX, out.toString(),
				template
		);
	}
	
	private void getFragment(String template, boolean complete, AttachmentSpecifier[] specifiers) {
		List<String> res = sources.get(template + ".fsh");
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[1] = new AttachmentSpecifier(
				AttachmentType.FRAGMENT, out.toString(),
				template
		);
	}
	
	private void getGeometry(String template, boolean complete, AttachmentSpecifier[] specifiers) {
		List<String> res = sources.get(template + ".gsh");
		if (res == null)
			return; // don't want to throw an error here, as you don't actually need a gsh
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[2] = new AttachmentSpecifier(
				AttachmentType.GEOMETRY, out.toString(),
				template
		);
	}
	
	public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, boolean complete, Function<String, TemplateTransformation> transformations) {
		Map<String, String> transformers = struct.getTransformers();
		String template = struct.getTemplate("optifine");
		// TODO: iris remap fallback
		if (template == null)
			return LoadResult.FAILED;

		try {
//			ShaderKey key = ShadowProgramMapper.getKey(template);
//			ShaderKey shadow = ShadowProgramMapper.getShadow(key);
			
			ShaderType key = ShaderType.getType(template);
			ShadowProgram shadow = ShadowProgram.SHADOW_CUTOUT;
			
			String templateShadow = shadow.getName();
			
			String pth = OptifineAccessor.dimensionShader();
			String gbuffer = pth + "gbuffers_";
			AttachmentSpecifier[] specifiers = new AttachmentSpecifier[5];
			try {
				System.out.println("DERIVE FROM: " + gbuffer + template);
				
				getVertex(gbuffer + template, complete, specifiers);
				getFragment(gbuffer + template, complete, specifiers);
				getGeometry(gbuffer + template, complete, specifiers);
			} catch (Throwable err) {
				return LoadResult.UNCACHED;
			}
			
			System.out.println(specifiers[0]);
			System.out.println(specifiers[1]);
			
			Program instance = instances.get("gbuffers_" + template);
			System.out.println(instance);
			if (specifiers[0] == null || specifiers[1] == null || instance == null)
				return LoadResult.UNCACHED;

			AttachmentSpecifier[] specifiersShadow = new AttachmentSpecifier[5];
			Program instanceShadow = instances.get(pth + templateShadow);
			{
				try {
					getVertex(templateShadow, complete, specifiersShadow);
					getFragment(templateShadow, complete, specifiersShadow);
					getGeometry(templateShadow, complete, specifiersShadow);
				} catch (Throwable err) {
				}
			}

			super.load(manager, new OptifineTemplatedShader(
					this, this,
					transformers, transformations,
					struct, processor,
					template, instance, specifiers,
					templateShadow, instanceShadow, specifiersShadow
			));

			return LoadResult.LOADED;
		} catch (Throwable err) {
			LOGGER.error("Failed loading template template " + struct.location + " for shader " + template, err);
			return LoadResult.FAILED;
		}
	}
	
	@Override
	public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Function<String, TemplateTransformation> transformations) {
		return attempt(manager, transformation, false, transformations);
	}
	
	@Override
	public String name() {
		return "optifine";
	}
	
	@Override
	public TransformationProcessor processor() {
		return processor;
	}
	
	@Override
	public boolean manuallyReloaded() {
		return true;
	}
	
	@Override
	public void _beginReload() {
		sources.clear();
		instances.clear();
		deferredLoad.clear();
	}
	
	@Override
	public void dumpShaders() {
		super.dumpShaders();
		_beginReload();
	}
	
	@Override
	public String mapFrom(String proposedType, String srcName) {
		return NameMapper.fromOF(proposedType, srcName);
	}
	
	@Override
	public String mapTo(String proposedType, String name) {
		return NameMapper.toOF(proposedType, name);
	}

//    public static void doLoad() {
//        for (String s : deferredLoad) {
//            // shadow passes aren't valid as bases
//            if (s.startsWith("shadow_")) continue;
//
//            TemplateManager.reloadTemplate(INSTANCE, s);
//        }
//        deferredLoad.clear();
//    }
	
	@Override
	public void prepare(ResourceManager manager) {
		// no operation; not bound to resource manager
	}
	
	@Override
	public void preload(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, Function<String, TemplateTransformation> transformations) {
		// no operation; not bound to resource manager
	}
}

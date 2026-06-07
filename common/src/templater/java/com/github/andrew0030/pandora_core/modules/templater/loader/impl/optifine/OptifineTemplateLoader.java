package com.github.andrew0030.pandora_core.modules.templater.loader.impl.optifine;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.modules.templater.NameMapper;
import com.github.andrew0030.pandora_core.modules.templater.TemplateManager;
import com.github.andrew0030.pandora_core.modules.templater.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.modules.templater.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.utils.toasts.icon.PaCoIcon;
import com.github.andrew0030.pandora_core.utils.toasts.PaCoToast;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.modules.templater.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.optifine.OptifineTemplatedShader;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.modules.templater.wrapper.impl.program.attachment.AttachmentType;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.shader_checker.optifine.OptifineAccessor;
import com.github.andrew0030.pandora_core.utils.toasts.background.ToastBackground;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.*;
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
//		System.out.println("SOURCE: " + ACTIVE);
		SOURCE.addAll($$1);
	}
	
	static boolean forceLoad = false;
	
	static Program activeProg;
	
	public static void link() {
//		System.out.println("LINK: " + ACTIVE);
		if (ACTIVE != null) {
			ReadOnlyList<String> rol = new ReadOnlyList<>(SOURCE);
			sources.put(ACTIVE, rol);
//			System.out.println("SOURCE FOR: " + ACTIVE.substring(ACTIVE.lastIndexOf(".")));
			sourcesByExt.put(
					Pair.of(
							ACTIVE.substring(ACTIVE.lastIndexOf(".")),
							activeProg
					),
					rol
			);
			progsWithSrcs.add(activeProg);
		}
	}
	
	public static void cancel() {
		ACTIVE = null;
	}
	
	private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Iris");
	
	private static final Map<String, Program> instances = new HashMap<>();
	private static final Map<Pair<String, Program>, List<String>> sourcesByExt = new HashMap<>();
	private static final Set<Program> progsWithSrcs = new HashSet<>();
	private static final Map<Program, String> names = new HashMap<>();
	private static final List<String> deferredLoad = new ArrayList<>();
	
	public static void bindShader(String $$1, Program shaderInstance) {
//		System.out.println("BIND: " + $$1);
		instances.put($$1, shaderInstance);
		activeProg = shaderInstance;
		names.put(shaderInstance, $$1);
		if (!forceLoad)
			deferredLoad.add($$1);
	}
	
	public static void unbindShader(String pandoraCore$cacheName, Program instance) {
		instances.remove(pandoraCore$cacheName, instance);
	}
	
	private void getVertex(String templateName, Program template, boolean complete, AttachmentSpecifier[] specifiers) {
//		System.out.println(template + " .vsh");
		List<String> res = sourcesByExt.get(Pair.of(".vsh", template));
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[0] = new AttachmentSpecifier(
				AttachmentType.VERTEX, out.toString(),
				templateName
		);
	}
	
	private void getFragment(String templateName, Program template, boolean complete, AttachmentSpecifier[] specifiers) {
//		System.out.println(template + " .fsh");
		List<String> res = sourcesByExt.get(Pair.of(".fsh", template));
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[1] = new AttachmentSpecifier(
				AttachmentType.FRAGMENT, out.toString(),
				templateName
		);
	}
	
	private void getGeometry(String templateName, Program template, boolean complete, AttachmentSpecifier[] specifiers) {
		List<String> res = sourcesByExt.get(Pair.of(".gsh", template));
		if (res == null)
			return; // don't want to throw an error here, as you don't actually need a gsh
		StringBuilder out = new StringBuilder();
		for (String re : res) out.append(re).append("\n");
		specifiers[2] = new AttachmentSpecifier(
				AttachmentType.GEOMETRY, out.toString(),
				templateName
		);
	}
	
	private Program resolveHighestProgram(Program prog) {
		while (true) {
//			System.out.println("RESOLVE: " + prog);
			if (progsWithSrcs.contains(prog)) {
				return prog;
			}
			Program bak = prog.getProgramBackup();
			if (bak == Shaders.ProgramNone)
				return null;
			prog = bak;
		}
	}
	
	boolean firstFail = false;
	
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
			Program instance = instances.get("gbuffers_" + template);
			Program refInstance = resolveHighestProgram(instance);
			try {
//				System.out.println("DERIVE FROM: " + gbuffer + template);
				
				getVertex(gbuffer + template, refInstance, complete, specifiers);
				getFragment(gbuffer + template, refInstance, complete, specifiers);
				getGeometry(gbuffer + template, refInstance, complete, specifiers);
			} catch (Throwable err) {
				return LoadResult.UNCACHED;
			}
			
//			System.out.println(specifiers[0]);
//			System.out.println(specifiers[1]);
			
//			System.out.println(instance);
			if (specifiers[0] == null || specifiers[1] == null || instance == null)
				return LoadResult.UNCACHED;
			
			String shadowTemplate = pth + templateShadow;
//			System.out.println("DERIVE SHADOW FROM: " + shadowTemplate);
			
			AttachmentSpecifier[] specifiersShadow = new AttachmentSpecifier[5];
			Program instanceShadow = instances.get(templateShadow);
			refInstance = resolveHighestProgram(instanceShadow);
			{
				try {
					getVertex(shadowTemplate, refInstance, complete, specifiersShadow);
					getFragment(shadowTemplate, refInstance, complete, specifiersShadow);
					getGeometry(shadowTemplate, refInstance, complete, specifiersShadow);
				} catch (Throwable err) {
				}
			}
//			System.out.println(specifiersShadow[0]);
//			System.out.println(specifiersShadow[1]);
//			System.out.println(instanceShadow);

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
			if (firstFail) {
				firstFail = false;
				TemplateManager.postToast(
						PaCoIcon.FORGE_20x20, ToastBackground.ERROR,
						"Shaders failed to load",
						"Objects may not render",
						PaCoIcon.PACO
				);
			}
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
		
		firstFail = true;
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
	
	@Override
	public void performReload() {
		PaCoToast toast = new PaCoToast(
				Component.literal("Optifine support is WIP"),
				Component.literal("There may be problems")
		).setColorTitle(16755200).setColorMessage(16777215);
		toast
				.setIcon(PaCoIcon.WARNING).setModIcon(PaCoIcon.PACO)
				.setBG(ToastBackground.NOTICE);
		
		Minecraft.getInstance().getToasts().addToast(toast);
		
		super.performReload();
	}
}

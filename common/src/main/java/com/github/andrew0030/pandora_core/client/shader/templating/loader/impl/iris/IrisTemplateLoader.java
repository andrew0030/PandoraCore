package com.github.andrew0030.pandora_core.client.shader.templating.loader.impl.iris;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.client.shader.templating.NameMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateManager;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateShaderResourceLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.ShaderCapabilities;
import com.github.andrew0030.pandora_core.client.shader.templating.loader.TemplateLoader;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.DefaultTransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.IrisTemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.TemplatedShader;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentSpecifier;
import com.github.andrew0030.pandora_core.client.shader.templating.wrapper.impl.program.attachment.AttachmentType;
import com.github.andrew0030.pandora_core.utils.collection.DualKeyMap;
import com.github.andrew0030.pandora_core.utils.collection.ReadOnlyList;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class IrisTemplateLoader extends TemplateLoader implements VariableMapper {
    private static String MOD = null;
    private static String ACTIVE = null;
    private static List<String> SOURCE = new ArrayList<>();

    private static DualKeyMap<String, String, List<String>> sources = new DualKeyMap<>(new HashMap<>());

    private final TransformationProcessor processor = new DefaultTransformationProcessor();

    private static IrisTemplateLoader INSTANCE;

    public IrisTemplateLoader() {
        super(ShaderCapabilities.CAPABILITIES_WORLD_SHADOW);
        if (INSTANCE != null)
            throw new RuntimeException("Cannot create two vanilla template loaders.");
        INSTANCE = this;
    }

    public static void activeFile(String source, String file) {
        MOD = source;
        ACTIVE = file;
        SOURCE = new ArrayList<>();
    }

    public static void shaderSource(List<String> $$1) {
        SOURCE.addAll($$1);
    }

    static boolean forceLoad = false;

    public static void link() {
        if (MOD != null)
            sources.put(MOD, ACTIVE, new ReadOnlyList<>(SOURCE));
    }

    public static void cancel() {
        MOD = null;
        ACTIVE = null;
    }

    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "Template Shaders", "Iris");

    private static final Map<String, ShaderInstance> instances = new HashMap<>();
    private static final List<String> deferredLoad = new ArrayList<>();

    public static void bindShader(String $$1, ShaderInstance shaderInstance) {
        instances.put($$1, shaderInstance);
        if (!forceLoad)
//            TemplateManager.reloadTemplate(INSTANCE, $$1);
            deferredLoad.add($$1);
    }

    public static void unbindShader(String pandoraCore$cacheName, ShaderInstance instance) {
        instances.remove(pandoraCore$cacheName, instance);
    }

    private void getVertex(String template, boolean complete, AttachmentSpecifier[] specifiers) {
        List<String> res = sources.get("minecraft", template + ".vsh");
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        specifiers[0] = new AttachmentSpecifier(
                AttachmentType.VERTEX, out.toString(),
                template
        );
    }

    private void getFragment(String template, boolean complete, AttachmentSpecifier[] specifiers) {
        List<String> res = sources.get("minecraft", template + ".fsh");
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        specifiers[1] = new AttachmentSpecifier(
                AttachmentType.FRAGMENT, out.toString(),
                template
        );
    }

    private void getGeometry(String template, boolean complete, AttachmentSpecifier[] specifiers) {
        List<String> res = sources.get("minecraft", template + ".gsh");
        if (res == null)
            return; // don't want to throw an error here, as you don't actually need a gsh
        StringBuilder out = new StringBuilder();
        for (String re : res) out.append(re).append("\n");
        specifiers[2] = new AttachmentSpecifier(
                AttachmentType.GEOMETRY, out.toString(),
                template
        );
    }

    @Override
    public boolean matches(TemplatedShader direct, String shader, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        TemplateShaderResourceLoader.TemplateStruct transformation = direct.transformation();
        String plate = transformation.getTemplate("iris");
        if (plate == null)
            return false;

        ResourceLocation loc = new ResourceLocation(shader);
        String mod = loc.getNamespace();
        String active = loc.getPath();

        if (mod.equals("minecraft") && active.equals(plate)) {
//            if (direct instanceof VanillaTemplatedShader vts)
//                return vts.matches("minecraft", plate + ".fsh");
            return true;
        }
        return false;
    }

    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct struct, boolean complete, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        String template = struct.getTemplate("iris");
        if (template == null)
            return LoadResult.FAILED;

        try {
            ShaderKey key = ShadowProgramMapper.getKey(template);
            ShaderKey shadow = ShadowProgramMapper.getShadow(key);

            String templateShadow = shadow.getName();

            AttachmentSpecifier[] specifiers = new AttachmentSpecifier[5];
            try {
                getVertex(template, complete, specifiers);
                getFragment(template, complete, specifiers);
                getGeometry(template, complete, specifiers);
            } catch (Throwable err) {
                return LoadResult.UNCACHED;
            }

            ShaderInstance instance = instances.get(template);
            if (specifiers[0] == null || specifiers[1] == null || instance == null)
                return LoadResult.UNCACHED;

            AttachmentSpecifier[] specifiersShadow = new AttachmentSpecifier[5];
            ShaderInstance instanceShadow = instances.get(templateShadow);
            {
                try {
                    getVertex(templateShadow, complete, specifiersShadow);
                    getFragment(templateShadow, complete, specifiersShadow);
                    getGeometry(templateShadow, complete, specifiersShadow);
                } catch (Throwable err) {
                }
            }

            manager.load(new IrisTemplatedShader(
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
    public LoadResult attempt(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, false, transformers, transformations);
    }

    @Override
    public boolean attemptComplete(TemplateManager.LoadManager manager, TemplateShaderResourceLoader.TemplateStruct transformation, Map<String, String> transformers, Function<String, TemplateTransformation> transformations) {
        return attempt(manager, transformation, true, transformers, transformations) == LoadResult.LOADED;
    }

    @Override
    public String name() {
        return "iris";
    }

    @Override
    public TransformationProcessor processor() {
        return processor;
    }

    @Override
    public void beginReload() {
        sources.clear();
        deferredLoad.clear();
    }

    @Override
    public String mapFrom(String proposedType, String srcName) {
        return NameMapper.fromIris(proposedType, srcName);
    }

    @Override
    public String mapTo(String proposedType, String name) {
        return NameMapper.toIris(proposedType, name);
    }

    public static void doLoad() {
        for (String s : deferredLoad) {
            // shadow passes aren't valid as bases
            if (s.startsWith("shadow_")) continue;

            TemplateManager.reloadTemplate(INSTANCE, s);
        }
        deferredLoad.clear();
    }

    @Override
    public void prepare(ResourceManager manager) {
        // no operation; not bound to resource manager
    }
}

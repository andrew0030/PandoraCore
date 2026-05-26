package com.github.andrew0030.pandora_core.modules.templater.transformer.impl;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.action.InsertionAction;
import com.github.andrew0030.pandora_core.modules.templater.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import io.github.ocelot.glslprocessor.impl.GlslParserImpl;
import tfc.glsl.GlslFile;
import tfc.glsl.GlslParser;

import java.util.HashMap;

public class DefaultTransformationProcessor extends TransformationProcessor {
    @Override
    public String process(VariableMapper mapper, String source, TemplateTransformation transformation) {
        try {
            source = GlslParserImpl.preprocess(source, new HashMap<>());

            GlslFile tree = GlslParser.parse(source);

            ShaderTransformer transformer = new ShaderTransformer(transformation);
            for (InsertionAction action : transformation.getActions()) {
                transformer.addAction(action);
            }
            transformer.transform(mapper, tree);

            return tree.asString();
        } catch (Throwable err) {
            throw new RuntimeException("Unexpected error while patching shader", err);
        }
    }
}

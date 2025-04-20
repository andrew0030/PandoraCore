package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.InsertionAction;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.TransformationProcessor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeStringWriter;

public class DefaultTransformationProcessor extends TransformationProcessor {
    @Override
    public String process(VariableMapper mapper, String source, TemplateTransformation transformation) {
        try {
            GlslTree tree = GlslParser.parse(source);

            ShaderTransformer transformer = new ShaderTransformer(transformation);
            for (InsertionAction action : transformation.getActions()) {
                transformer.addAction(action);
            }
            transformer.transform(mapper, tree);

            GlslTreeStringWriter writer = new GlslTreeStringWriter();
            tree.visit(writer);
            return writer.toString();
        } catch (Throwable err) {
            throw new RuntimeException("Unexpected error while patching shader", err);
        }
    }
}

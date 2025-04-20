package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.InsertionAction;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.patches.WrapFieldNode;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeStringWriter;

import java.util.ArrayList;
import java.util.List;

public class ShaderTransformer {
    TemplateTransformation transformation;
    List<InsertionAction> actions = new ArrayList<>();

    public ShaderTransformer(TemplateTransformation transformation) {
        this.transformation = transformation;
    }

    public void addAction(InsertionAction action) {
        this.actions.add(action);
    }

    public void transform(VariableMapper mapper, GlslTree tree) {
        int index = 0;
        int size = tree.getBody().size();

        for (InsertionAction action : actions) {
            String injection = action.headInjection(transformation);
            if (injection != null) {
                tree.getDirectives().add(injection);
            }
        }

        for (int i = 0; i < size; i++) {
            GlslNode glslNode = tree.getBody().get(index);

            if (glslNode instanceof GlslNewFieldNode field) {
                String name = field.getName();
                for (InsertionAction action : actions) {
                    String insertion = action.afterInputVar(
                            mapper, transformation,
                            field.getType().getSpecifier().getName(), name
                    );
                    if (insertion != null) {
                        tree.getBody().add(index+1, new GlslNewFieldNode(
                                GlslTypeSpecifier.named(""),
                                insertion + "\n;",
                                null
                        ));
                        index++;
                    }
                }
            }
            index++;
        }
    }
}

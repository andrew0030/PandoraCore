package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.TransformationContext;
import com.mojang.datafixers.util.Pair;
import tfc.glsl.base.GlslSegment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class InsertionAction {
    protected Set<TransformVar.Operation> OPS = new HashSet<>();

    public List<GlslSegment> headInjection(TemplateTransformation transformation, VariableMapper mapper, TransformationContext context) {
        return null;
    }

    public Pair<List<GlslSegment>, String> transformInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var, TransformationContext context) {
        return Pair.of(afterInputVar(mapper, transformation, type, var, context), null);
    }

    public List<GlslSegment> afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var, TransformationContext context) {
        return null;
    }

    public boolean hasQuatRot() {
        return OPS.contains(TransformVar.Operation.ROTATE_QUAT);
    }

    public boolean hasMatrTranslate() {
        return OPS.contains(TransformVar.Operation.TRANSLATE_MATRIX);
    }

    public boolean hasMatrRotate() {
        return OPS.contains(TransformVar.Operation.ROTATE_MATRIX);
    }
}

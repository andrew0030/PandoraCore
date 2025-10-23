package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.mojang.datafixers.util.Pair;
import tfc.glsl.base.GlslSegment;

import java.util.List;

public abstract class InsertionAction {
    public List<GlslSegment> headInjection(TemplateTransformation transformation) {
        return null;
    }

    public Pair<List<GlslSegment>, String> transformInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var) {
        return Pair.of(afterInputVar(mapper, transformation, type, var), null);
    }

    public List<GlslSegment> afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var) {
        return null;
    }
}

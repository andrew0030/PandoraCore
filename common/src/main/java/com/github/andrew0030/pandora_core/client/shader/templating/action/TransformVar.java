package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.util.OperationVisitor;
import com.github.andrew0030.pandora_core.client.shader.templating.action.util.Transformations;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.TransformationContext;
import com.mojang.datafixers.util.Pair;
import tfc.glsl.base.GlslSegment;
import tfc.glsl.base.GlslValue;
import tfc.glsl.meta.VarSpecifier;
import tfc.glsl.segments.GlslVarSegment;

import java.util.ArrayList;
import java.util.List;

public class TransformVar extends InsertionAction {
    VarSpecifier from;
    GlslValue to;

    public TransformVar(VarSpecifier from, GlslValue to) {
        this.from = from;
        this.to = to;
        new OperationVisitor(OPS).visitValue(to);
    }

    @Override
    public Pair<List<GlslSegment>, String> transformInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var, TransformationContext context) {
        String snowflake = transformation.generateSnowflake() + "_" + var;
        return Pair.of(
                afterInputVar(
                        mapper, transformation,
                        type, var,
                        snowflake,
                        context
                ),
                snowflake
        );
    }

    @Override
    public List<GlslSegment> afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var, TransformationContext context) {
        String snowflake = transformation.generateSnowflake() + "_" + var;
        return afterInputVar(mapper, transformation, type, var, snowflake, context);
    }

    protected GlslValue patch(GlslValue value, TemplateTransformation transformation, VariableMapper mapper, String fromName, String toName, TransformationContext context) {
        Transformations.callPatcher(
                transformation,
                context
        ).visitValue(value);
        Transformations.valuePatcher(
                transformation, mapper,
                fromName, toName, context
        ).visitValue(value);
        return value;
    }

    public List<GlslSegment> afterInputVar(VariableMapper mapper, TemplateTransformation transformation, String type, String var, String snowflake, TransformationContext context) {
        String varMap = mapper.mapFrom(type, var);
        if (!varMap.equals(from.getName()))
            return null;

        List<GlslSegment> ret = new ArrayList<>();
        ret.add(new GlslVarSegment(new VarSpecifier(
                type,
                snowflake
        )).setValue(patch(to.duplicate(), transformation, mapper, from.getName(), var, context)));
        return ret;
    }

    public enum Operation {
        ADD("add", "(%lh%+%rh%)"),
        SUB("subtract", "(%lh%-%rh%)"),
        MUL("multiply", "(%lh%*%rh%)"),
        DIV("divide", "(%lh%/%rh%)"),
        lhDIV("lhDivide", "(%rh%/%lh%)"),
        lhMUL("lhMultiply", "(%rh%+%lh%)"),
        lhSUB("lhSubtract", "(%rh%+%lh%)"),
        ROTATE_QUAT("rotateQuat", "%func%(%lh%,%rh%)", "rotateQuat"),
        TRANSLATE_MATRIX("translateMatr", "%func%(%lh%,%rh%)", "translateMatr"),
        ROTATE_MATRIX("rotateMatr", "%func%(%lh%,%rh%)", "rotateMatr"),
        ;

        final String name;
        final String format;
        final String func;

        protected static final Operation[] VALUES = values();

        Operation(String name, String format) {
            this.name = name;
            this.format = format;
            this.func = null;
        }

        Operation(String name, String format, String func) {
            this.name = name;
            this.format = format;
            this.func = func;
        }

        public static Operation forName(String s) {
            for (Operation value : VALUES) {
                if (value.name.equals(s)) {
                    return value;
                }
            }
            return null;
        }
    }
}

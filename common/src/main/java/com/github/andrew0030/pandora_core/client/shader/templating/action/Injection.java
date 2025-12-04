package com.github.andrew0030.pandora_core.client.shader.templating.action;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.util.OperationVisitor;
import com.github.andrew0030.pandora_core.client.shader.templating.action.util.Transformations;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl.TransformationContext;
import tfc.glsl.base.GlslSegment;
import tfc.glsl.base.GlslValue;
import tfc.glsl.base.SegmentType;
import tfc.glsl.meta.Member;
import tfc.glsl.meta.VarSpecifier;
import tfc.glsl.segments.GlslCodeSegment;
import tfc.glsl.segments.GlslMemberSegment;
import tfc.glsl.segments.GlslVarSegment;
import tfc.glsl.value.AccessArrayValue;
import tfc.glsl.value.ConstantValue;
import tfc.glsl.value.MethodCallValue;
import tfc.glsl.value.TokenValue;
import tfc.glsl.visitor.GlslTreeVisitor;
import tfc.glsl.visitor.GlslValueVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Injection extends InsertionAction {
    List<GlslSegment> segments;

    public Injection(List<GlslSegment> segments) {
        this.segments = segments;
        GlslTreeVisitor visitor = new GlslTreeVisitor(
                new OperationVisitor(OPS),
                null, null
        );
        for (GlslSegment segment : segments) {
            visitor.visit(segment);
        }
    }

    // TODO: patch nested function calls
    private GlslCodeSegment patchFunction(TemplateTransformation transformation, GlslCodeSegment segment, TransformationContext context) {
        String snowflake = transformation.generateSnowflake() + "_" + segment.getName();
        GlslCodeSegment dup = (GlslCodeSegment) segment.duplicate();
        dup.setName(snowflake);
        context.setFuncName(segment.getName(), dup.getName());
        GlslValueVisitor valueVisitor = Transformations.callPatcher(
                transformation, context
        );
        GlslTreeVisitor visitor = new GlslTreeVisitor(
                valueVisitor, null, null
        );
        visitor.visit(dup);
        return dup;
    }

    private GlslVarSegment patchVar(TemplateTransformation transformation, GlslVarSegment segment, VariableMapper mapper, TransformationContext context) {
        GlslVarSegment dup = (GlslVarSegment) segment.duplicate();
        if (dup.getValue() != null) {
            Transformations.callPatcher(
                    transformation,
                    context
            ).visitValue(dup.getValue());
            Transformations.valuePatcher(
                    transformation,
                    mapper,
                    null, null,
                    context
            ).visitValue(dup.getValue());
        }
        return dup;
    }

    protected List<GlslSegment> patchSegments(TemplateTransformation transformation, VariableMapper mapper, TransformationContext context) {
        List<GlslSegment> copies = new ArrayList<>();
        for (GlslSegment segment : segments) {
            if (
                    segment.getSegmentType().equals(SegmentType.MEMBER_DEF)
            ) {
                copies.addAll(patchMember((GlslMemberSegment) segment));
            } else if (
                    segment.getSegmentType().equals(SegmentType.CODE)
            ) {
                copies.add(patchFunction(transformation, (GlslCodeSegment) segment, context));
            } else if (
                    segment.getSegmentType().equals(SegmentType.VAR_DEF)
            ) {
                copies.add(patchVar(transformation, (GlslVarSegment) segment, mapper, context));
            } else {
                copies.add(segment);
            }
        }
        return copies;
    }

    private List<GlslSegment> patchMatrix(GlslMemberSegment segment) {
        String matrType = segment.getMember().getVar().getType();
        char vecType = matrType.charAt(matrType.length() - 1);
        int matrWidth = (int) (matrType.charAt(3) - '0');

        List<GlslSegment> dup = new ArrayList<>();
        List<GlslValue> params = new ArrayList<>();
        for (int i = 0; i < matrWidth; i++) {
            GlslMemberSegment vec = new GlslMemberSegment(
                    segment.getQualifier(),
                    new Member(
                            new VarSpecifier(
                                    "vec" + vecType,
                                    segment.getMember().getVar().getName() + "_" + i
                            )
                    ).setLayout(segment.getMember().getLayout())
            );

            // TODO: test and optimize, lol
            if (segment.getValue() != null)
                vec.setValue(
                        new AccessArrayValue(
                                segment.getValue().duplicate(),
                                new ConstantValue(i)
                        )
                );
            dup.add(vec);

            params.add(new TokenValue(
                    segment.getMember().getVar().getName() + "_" + i
            ));
        }
        GlslVarSegment varSeg = new GlslVarSegment(
                new VarSpecifier(
                        segment.getMember().getVar().getType(),
                        segment.getMember().getVar().getName()
                )
        );
        MethodCallValue cv = new MethodCallValue(
                new TokenValue(segment.getMember().getVar().getType()),
                params.toArray(new GlslValue[0])
        );
        varSeg.setValue(cv);
        dup.add(varSeg);
        return dup;
    }

    private List<GlslSegment> patchMember(GlslMemberSegment segment) {
        if (segment.getMember().getVar().getType().startsWith("mat")) {
            return patchMatrix(segment);
        }
        return List.of(segment);
//        GlslMemberSegment dup = new GlslMemberSegment(
//                segment.getQualifier(),
//                new Member(
//                        new VarSpecifier(
//                                segment.getMember().getVar().getType(),
//                                segment.getMember().getVar().getName()
//                        )
//                ).setLayout(segment.getMember().getLayout())
//        );
//        if (segment.getValue() != null)
//            dup.setValue(
//                    segment.getValue().duplicate()
//            );
//        return List.of(dup);
    }

    @Override
    public List<GlslSegment> headInjection(TemplateTransformation transformation, VariableMapper mapper, TransformationContext context) {
        return patchSegments(transformation, mapper, context);
    }

    public void resolveTypes(HashMap<String, String> varTypes) {
        for (GlslSegment segment : segments) {
            switch (segment.getSegmentType()) {
                case VAR_DEF -> {
                    GlslVarSegment var = (GlslVarSegment) segment;
                    varTypes.put(var.getVar().getName(), var.getVar().getType());
                }
                case MEMBER_DEF -> {
                    GlslMemberSegment var = (GlslMemberSegment) segment;
                    varTypes.put(var.getMember().getVar().getName(), var.getMember().getVar().getType());
                }
                default -> {
                    // no-op
                }
            }
        }
    }

    protected String transformMatrix(String first, String matrType, String name) {
        throw new RuntimeException("TODO");
//        StringBuilder builder = new StringBuilder();
//
//        char vecType = matrType.charAt(matrType.length() - 1);
//        int matrWidth = (int) (matrType.charAt(3) - '0');
//
//        for (int i = 0; i < matrWidth; i++) {
//            builder.append(first).append(" ")
//                    .append("vec").append(vecType).append(" ")
//                    .append(name).append("_").append(i).append(";\n");
//        }
//        builder.append(matrType).append(" ")
//                .append(name).append(" = ").append(matrType).append("(");
//        for (int i = 0; i < matrWidth; i++) {
//            builder.append(name).append("_").append(i);
//            if (i != matrWidth - 1)
//                builder.append(", ");
//        }
//        builder.append(");\n");
//
//        return builder.toString();
    }

    public String transformTypes() {
        throw new RuntimeException("TODO");
//        ShaderFile file = ShaderParser.parse(text);
//        StringBuilder builder = new StringBuilder();
//        for (Line line : file.lines()) {
//            String trim = line.text.trim();
//            if (
//                    trim.startsWith("in") ||
//                            trim.startsWith("paco_per_instance") ||
//                            trim.startsWith("uniform")
//            ) {
//                List<String> strs = line.resolveInputVar();
//
//                if (strs.size() >= 3) {
//                    String type = strs.get(1);
//
//                    if (type.startsWith("mat")) {
//                        builder.append(transformMatrix(strs.get(0), strs.get(1), strs.get(2)));
//                    } else {
//                        for (int i = 0; i < strs.size(); i++) {
//                            builder.append(strs.get(i)).append(" ");
//                        }
//                        builder.append(";\n");
//                    }
//                }
//            }
//        }
//        return builder.toString();
    }
}

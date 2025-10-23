package com.github.andrew0030.pandora_core.client.shader.templating.transformer.impl;

import com.github.andrew0030.pandora_core.client.shader.templating.TemplateTransformation;
import com.github.andrew0030.pandora_core.client.shader.templating.action.InsertionAction;
import com.github.andrew0030.pandora_core.client.shader.templating.action.util.RemappingVisitor;
import com.github.andrew0030.pandora_core.client.shader.templating.transformer.VariableMapper;
import com.mojang.datafixers.util.Pair;
import tfc.glsl.GlslFile;
import tfc.glsl.base.GlslSegment;
import tfc.glsl.base.SegmentType;
import tfc.glsl.segments.GlslMemberSegment;
import tfc.glsl.visitor.GlslTreeVisitor;

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

    public void transform(VariableMapper mapper, GlslFile tree) {
        for (InsertionAction action : actions) {
            List<GlslSegment> segments = action.headInjection(transformation);
            if (segments == null) continue;

            for (int i = segments.size() - 1; i >= 0; i--) {
                tree.getSegments().add(0, segments.get(i));
            }
        }

        for (int index = 0; index < tree.getSegments().size(); index++) {
            GlslSegment segment = tree.getSegments().get(index);

            for (InsertionAction action : actions) {
                if (segment.getSegmentType() == SegmentType.MEMBER_DEF) {
                    GlslMemberSegment memberSegment = (GlslMemberSegment) segment;
                    Pair<List<GlslSegment>, String> segments = action.transformInputVar(
                            mapper,
                            transformation,
                            memberSegment.getMember().getVar().getType(),
                            memberSegment.getMember().getVar().getName()
                    );
                    if (segments.getFirst() == null) continue;

                    boolean map = segments.getSecond() != null;
                    if (map) {
                        RemappingVisitor visitor = new RemappingVisitor(
                                memberSegment.getMember().getVar().getName(),
                                segments.getSecond()
                        );
                        GlslTreeVisitor treeVisitor = new GlslTreeVisitor(
                                visitor,
                                null,
                                null
                        );
                        for (int index1 = index + 1; index1 < tree.getSegments().size(); index1++) {
                            GlslSegment segment1 = tree.getSegments().get(index1);
                            treeVisitor.visit(segment1);
                        }
                    }

                    for (int i = segments.getFirst().size() - 1; i >= 0; i--) {
                        tree.getSegments().add(index + 1, segments.getFirst().get(i));
                    }
                }
            }
        }

        System.out.println("QWERTYUIOP");
    }
}

package com.github.andrew0030.pandora_core.modules.templater.transformer.impl;

import com.github.andrew0030.pandora_core.modules.templater.TemplateTransformation;
import com.github.andrew0030.pandora_core.modules.templater.action.InsertionAction;
import com.github.andrew0030.pandora_core.modules.templater.action.util.RemappingVisitor;
import com.github.andrew0030.pandora_core.modules.templater.compat.CompatPrePatcher;
import com.github.andrew0030.pandora_core.modules.templater.compat.ImmersivePortalsPrePatcher;
import com.github.andrew0030.pandora_core.modules.templater.transformer.VariableMapper;
import com.github.andrew0030.pandora_core.platform.Services;
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

	private static final List<CompatPrePatcher> prepatchers = new ArrayList<>();
	
	static {
		if (Services.PLATFORM.isModLoaded("imm_ptl_core")) {
			prepatchers.add(new ImmersivePortalsPrePatcher());
		}
	}
	
    public ShaderTransformer(TemplateTransformation transformation) {
        this.transformation = transformation;
    }

    public void addAction(InsertionAction action) {
        this.actions.add(action);
    }

    public void transform(VariableMapper mapper, GlslFile tree) {
        TransformationContext context = new TransformationContext();
	    
	    for (CompatPrePatcher prepatcher : prepatchers) {
		    prepatcher.apply(mapper, tree, transformation);
	    }

        List<GlslSegment> HEAD = new ArrayList<>();
        for (InsertionAction action : actions) {
//        for (int i = actions.size() - 1; i >= 0; i--) {
//            InsertionAction action = actions.get(i);
            List<GlslSegment> segments = action.headInjection(transformation, mapper, context);
            if (segments == null) continue;

//            for (int i1 = segments.size() - 1; i1 >= 0; i1--) {
//                tree.getSegments().add(0, segments.get(i1));
//            }
            HEAD.addAll(segments);
        }
        tree.getSegments().addAll(0, HEAD);

        for (int index = 0; index < tree.getSegments().size(); index++) {
            GlslSegment segment = tree.getSegments().get(index);

            for (InsertionAction action : actions) {
                if (segment.getSegmentType() == SegmentType.MEMBER_DEF) {
                    GlslMemberSegment memberSegment = (GlslMemberSegment) segment;
                    Pair<List<GlslSegment>, String> segments = action.transformInputVar(
                            mapper,
                            transformation,
                            memberSegment.getMember().getVar().getType(),
                            memberSegment.getMember().getVar().getName(),
                            context
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
		
		tree.fixDirectives();

//        System.out.println("QWERTYUIOP");
    }
}

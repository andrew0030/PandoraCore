package com.github.andrew0030.pandora_core.client.shader.templating;

import com.github.andrew0030.pandora_core.client.shader.templating.action.Injection;
import com.github.andrew0030.pandora_core.client.shader.templating.action.TransformVar;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import tfc.glsl.GlslFile;
import tfc.glsl.GlslParser;
import tfc.glsl.base.GlslSegment;
import tfc.glsl.meta.Member;
import tfc.glsl.segments.GlslBlockSegment;
import tfc.glsl.segments.GlslMemberSegment;
import tfc.glsl.segments.GlslVarSegment;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class TemplateTransformationParser {
    public TemplateTransformationParser() {
    }

    public TemplateTransformation parse(ResourceLocation location, String text) {
        TemplateTransformation transformation = new TemplateTransformation(location);

        GlslFile file = GlslParser.parse("#version 1\n" + text);
        for (GlslSegment segment : file.getSegments()) {
            switch (segment.getSegmentType()) {
                case BLOCK_DEF -> {
                    GlslBlockSegment block = (GlslBlockSegment) segment;
                    if (block.getName().equals("PerInstance")) {
                        List<GlslSegment> segments = new ArrayList<>();

                        for (Member member : block.getMembers()) {
                            segments.add(
                                    new GlslMemberSegment(
                                            block.getType(),
                                            member
                                    )
                            );
                        }

                        transformation.actions.add(new Injection(
                                segments
                        ));
                    } else {
                        transformation.actions.add(new Injection(List.of(segment)));
                    }
                }
                case VAR_DEF -> {
                    GlslVarSegment member = (GlslVarSegment) segment;

                    switch (member.getVar().getType()) {
                        case "transform" -> {
                            transformation.actions.add(new TransformVar(
                                    member.getVar(),
                                    member.getValue()
                            ));
                        }
                        case "replace" -> {
//                            transformation.actions.add(new ReplaceVar(
//                                    member.getVar(),
//                                    member.getValue()
//                            ));

                            // for now...
                            // replace var is supposed to be a bit more bullet proof though, so I'll need to swap to that at some point
                            transformation.actions.add(new TransformVar(
                                    member.getVar(),
                                    member.getValue()
                            ));
                        }
                        default -> {
                            transformation.actions.add(new Injection(
                                    List.of(segment)
                            ));
                        }
                    }
                }
                default -> {
                    throw new RuntimeException("wat " + segment.getSegmentType());
                }
            }
        }
//        String blockType = null;
//        String block = "";
//
//        for (String s : text.split("\n")) {
//            String trim = s.trim();
//
//            if (blockType != null) {
//                // end block directive (inject&undef) parsing
//                if (trim.startsWith("#paco_end")) {
////                    transformation.actions.add(switch (blockType) {
////                        case "inject" -> new Injection(block);
////                        case "undef" -> new Undef(block);
////                        default -> throw new RuntimeException("???");
////                    });
//                    block = "";
//                    blockType = null;
//                } else {
//                    block += s + "\n";
//                }
//
//                continue;
//            }
//
//            // skip comments
//            // TODO: support block comments (/* */)
//            if (trim.startsWith("//"))
//                continue;
//
//            // parse normal (single line) directives
//            if (trim.startsWith("#paco_")) {
//                int indx;
//                String proc = trim.substring(0, (indx = trim.indexOf(" ")) == -1 ? trim.length() : indx);
//
//                switch (proc) {
//                    case "#paco_templated" -> {
//                        String[] split = trim.split(" ");
//                        transformation.templates.put(split[1], split[2]);
//                    }
//                    case "#paco_replace" -> {
//                        String[] split = trim.split(" ");
//                        transformation.actions.add(new ReplaceVar(split[1], split[2]));
//                    }
//                    case "#paco_transform" -> {
//                        String[] split = trim.split(" ", 3);
//                        transformation.actions.add(new TransformVar(split[1].replace(":", ""), split[2]));
//                    }
//                    case "#paco_inject" -> blockType = "inject";
//                    case "#paco_undef" -> blockType = "undef";
//                }
//            }
//        }

        transformation.lock();
        return transformation;
    }
}
